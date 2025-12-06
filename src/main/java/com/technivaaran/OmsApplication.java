package com.technivaaran;

import java.nio.file.Paths;

import com.technivaaran.util.BackupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OmsApplication {

	private static final Logger logger = LoggerFactory.getLogger(OmsApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OmsApplication.class, args);
	}

	// write a runner to take db backup when server starts
	//@Bean
	public CommandLineRunner backupDatabase() {
		return args -> {
			// run the Java backup implementation asynchronously to avoid blocking startup
			new Thread(() -> {
				try {
					boolean ok = BackupService.runBackup(Paths.get(System.getProperty("user.dir")));
					if (ok) {
						logger.info("Backup completed successfully (java backup)");
					} else {
						logger.error("Backup reported failures (java backup)");
					}
				} catch (Exception e) {
					logger.error("Unexpected error while running java backup", e);
				}
			}, "oms-backup-runner").start();
		};
	}

}
