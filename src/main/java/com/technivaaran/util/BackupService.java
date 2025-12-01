package com.technivaaran.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cross-platform backup service implemented in Java.
 * - Creates a ZIP archive of the repository (excludes build artifacts)
 * - If mysqldump is available and DB config present in src/main/resources/application.properties,
 *   runs mysqldump to produce a SQL dump alongside the ZIP.
 */
public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private static final Set<String> DEFAULT_EXCLUDES = new HashSet<>();

    static {
        DEFAULT_EXCLUDES.add("target");
        DEFAULT_EXCLUDES.add("backups");
        DEFAULT_EXCLUDES.add(".git");
        DEFAULT_EXCLUDES.add(".idea");
        DEFAULT_EXCLUDES.add("node_modules");
    }

    /**
     * Fallback DB dump implemented in Java using JDBC. Produces a SQL file with DROP/CREATE and INSERT statements.
     * Returns true on success.
     */
    private static boolean dumpDatabaseViaJdbc(String jdbcUrl, String user, String pass, Path outFile) {
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:mysql")) {
            logger.info("JDBC dump: not a MySQL JDBC URL ({}), skipping JDBC dump", jdbcUrl);
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8)) {
            // connect using provided credentials
            try (Connection conn = DriverManager.getConnection(jdbcUrl, user == null ? "" : user, pass == null ? "" : pass)) {
                DatabaseMetaData meta = conn.getMetaData();
                String catalog = conn.getCatalog();

                // iterate tables
                try (ResultSet tables = meta.getTables(catalog, null, "%", new String[] { "TABLE" })) {
                    while (tables.next()) {
                        String table = tables.getString("TABLE_NAME");
                        // write DROP TABLE
                        writer.write("DROP TABLE IF EXISTS `" + table + "`;\n");

                        // get create statement
                        try (Statement st = conn.createStatement(); ResultSet cr = st.executeQuery("SHOW CREATE TABLE `" + table + "`")) {
                            if (cr.next()) {
                                String create = cr.getString(2);
                                writer.write(create + ";\n\n");
                            }
                        }

                        // dump rows via SELECT
                        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM `" + table + "`")) {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int cols = rsmd.getColumnCount();
                            while (rs.next()) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("INSERT INTO `").append(table).append("` (");
                                for (int i = 1; i <= cols; i++) {
                                    sb.append("`").append(rsmd.getColumnName(i)).append("`");
                                    if (i < cols) sb.append(",");
                                }
                                sb.append(") VALUES (");
                                for (int i = 1; i <= cols; i++) {
                                    Object v = rs.getObject(i);
                                    if (v == null) {
                                        sb.append("NULL");
                                    } else if (v instanceof Number) {
                                        sb.append(v.toString());
                                    } else if (v instanceof Boolean) {
                                        sb.append(((Boolean) v) ? 1 : 0);
                                    } else {
                                        String s = rs.getString(i).replace("'", "''");
                                        sb.append('\'').append(s).append('\'');
                                    }
                                    if (i < cols) sb.append(",");
                                }
                                sb.append(");\n");
                                writer.write(sb.toString());
                            }
                        }

                        writer.write("\n");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("JDBC dump failed", e);
            return false;
        }

        return true;
    }

    /**
     * Run the backup for the given project root.
     * Returns true when both files archive and optional DB dump (if attempted) finished successfully.
     */
    public static boolean runBackup(Path projectRoot) {
        String ts = LocalDateTime.now().format(TS);
        Path backupsDir = projectRoot.resolve("backups");
        try {
            if (!Files.exists(backupsDir)) {
                Files.createDirectories(backupsDir);
            }
        } catch (IOException e) {
            logger.error("Unable to create backups directory: {}", backupsDir, e);
            return false;
        }

        Path zipPath = backupsDir.resolve("oms-backup-" + ts + ".zip");

        logger.info("Backup start: {} -> {}", ts, zipPath);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walkFileTree(projectRoot, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (shouldExclude(file, projectRoot)) return FileVisitResult.CONTINUE;
                    Path rel = projectRoot.relativize(file);
                    ZipEntry entry = new ZipEntry(rel.toString().replace(File.separatorChar, '/'));
                    zos.putNextEntry(entry);
                    try (InputStream in = Files.newInputStream(file)) {
                        copy(in, zos);
                    }
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (shouldExclude(dir, projectRoot)) return FileVisitResult.SKIP_SUBTREE;
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Failed to create files archive {}", zipPath, e);
            return false;
        }

        logger.info("Files archive created: {}", zipPath);

        // Attempt DB dump if possible
        Path props = projectRoot.resolve("src/main/resources/application.properties");
        if (Files.exists(props)) {
            logger.info("Reading DB settings from {}", props);
            try {
                Properties p = new Properties();
                try (InputStream in = Files.newInputStream(props)) {
                    p.load(in);
                }

                String url = p.getProperty("spring.datasource.url");
                String user = p.getProperty("spring.datasource.username");
                String pass = p.getProperty("spring.datasource.password");

                if (url != null && url.startsWith("jdbc:mysql")) {
                    // parse host, port and database
                    String host = "localhost";
                    String port = "3306";
                    String db = null;

                    try {
                        // jdbc:mysql://host:port/db?params
                        String s = url.substring("jdbc:mysql://".length());
                        int slash = s.indexOf('/');
                        String hostPort = s.substring(0, slash);
                        int colon = hostPort.indexOf(':');
                        if (colon > 0) {
                            host = hostPort.substring(0, colon);
                            port = hostPort.substring(colon + 1);
                        } else {
                            host = hostPort;
                        }
                        String rest = s.substring(slash + 1);
                        int q = rest.indexOf('?');
                        db = q > 0 ? rest.substring(0, q) : rest;
                    } catch (Exception ex) {
                        logger.warn("Failed to parse MySQL URL '{}', skipping DB dump", url);
                    }

                    if (db != null) {
                        // check for mysqldump on PATH
                        String exe = findExecutable("mysqldump");
                        if (exe == null) {
                            logger.info("mysqldump not found on PATH; attempting JDBC dump as a fallback");
                            // attempt JDBC-based dump as a fallback
                            try {
                                boolean jdbcOk = dumpDatabaseViaJdbc(url, user, pass, backupsDir.resolve("oms-db-" + ts + ".sql"));
                                if (jdbcOk) {
                                    logger.info("DB dump (JDBC) written to {}", backupsDir.resolve("oms-db-" + ts + ".sql"));
                                } else {
                                    logger.info("JDBC DB dump was not performed or failed");
                                }
                            } catch (Exception e) {
                                logger.warn("JDBC DB dump failed", e);
                            }
                        } else {
                            Path sqlOut = backupsDir.resolve("oms-db-" + ts + ".sql");
                            ProcessBuilder pb = new ProcessBuilder();
                            pb.command(exe, "-h", host, "-P", port, "-u", user == null ? "" : user, "--password=" + (pass == null ? "" : pass), db);
                            pb.redirectOutput(sqlOut.toFile());
                            pb.redirectErrorStream(true);
                            try {
                                logger.info("Running mysqldump to {}", sqlOut);
                                Process pr = pb.start();
                                boolean finished = pr.waitFor(5, java.util.concurrent.TimeUnit.MINUTES);
                                if (!finished) {
                                    pr.destroyForcibly();
                                    logger.error("mysqldump did not finish within timeout");
                                    return false;
                                }
                                int exit = pr.exitValue();
                                if (exit == 0) {
                                    logger.info("DB dump written to {}", sqlOut);
                                } else {
                                    logger.error("mysqldump finished with exit code {}", exit);
                                }
                            } catch (IOException | InterruptedException e) {
                                logger.error("Failed to run mysqldump", e);
                                Thread.currentThread().interrupt();
                                return false;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                logger.warn("Failed to read application.properties to attempt DB dump", e);
            }
        } else {
            logger.info("No application.properties at {} - skipping DB dump detection", props);
        }

        logger.info("Backup finished: {}", ts);
        return true;
    }

    private static boolean shouldExclude(Path path, Path root) {
        Path rel = root.relativize(path);
        for (Path part : rel) {
            if (DEFAULT_EXCLUDES.contains(part.toString())) return true;
        }
        return false;
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int r;
        while ((r = in.read(buf)) != -1) {
            out.write(buf, 0, r);
        }
    }

    /**
     * Try to find an executable on PATH. Returns full name (may be with .exe on Windows) or null.
     */
    private static String findExecutable(String baseName) {
        String path = System.getenv("PATH");
        if (path == null) return null;
        String[] parts = path.split(File.pathSeparator);
        boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
        for (String p : parts) {
            Path candidate = Paths.get(p, baseName + (isWin ? ".exe" : ""));
            if (Files.isExecutable(candidate)) return candidate.toAbsolutePath().toString();
            // sometimes executable without extension on unix
            candidate = Paths.get(p, baseName);
            if (Files.isExecutable(candidate)) return candidate.toAbsolutePath().toString();
        }
        return null;
    }
}
