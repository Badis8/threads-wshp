package com.javaws.threads.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.javaws.threads.service.FileWorker;
import com.javaws.threads.service.NaiveCypher;
import com.javaws.threads.service.Worker;

public class Program {

	private static List<File> getDirFiles(File dir) {
		List<String> paths = Arrays.asList(dir.list());
		List<File> files = new ArrayList<File>();

		for (String path : paths) {
			File f = Path.of(dir.getAbsolutePath(), path).toFile();
			if (f.isFile()) {
				files.add(f);
			} else if (f.isDirectory()) {
				files.addAll(getDirFiles(f));
			}
		}

		return files;
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("No commands passed");
			System.exit(0);
		}

		if (!("encrypt".equals(args[0]) || "decrypt".equals(args[0]))) {
			System.out.println("Unknown command " + args[0]);
			System.exit(0);
		}

		if (args.length == 1) {
			System.out.println("No files passed");
			System.exit(0);
		}

		NaiveCypher naiveCypher = new NaiveCypher("abcdedf", StandardCharsets.UTF_8);

		Worker worker = new FileWorker(naiveCypher, naiveCypher);

		try {
			List<File> files = new ArrayList<File>();

			for (int i = 1; i < args.length; i++) {

				File f = new File(args[i]);

				if (!f.exists()) {
					System.err.println("File " + args[i] + " do not exists.");
					System.exit(1);
				}

				if (f.isFile()) {
					files.add(f);

				} else if (f.isDirectory()) {
					files.addAll(getDirFiles(f));

				}
			}

			if ("encrypt".equals(args[0])) {
				System.out.println("Start encrypting ...");

				long startTime = System.currentTimeMillis();

				worker.encrypt(files);

				long endTime = System.currentTimeMillis();

				System.out.println("That took " + (endTime - startTime) + " milliseconds ... Kiss goodbye !");
			} else if ("decrypt".equals(args[0])) {
				System.out.println("Start decrypting ...");

				long startTime = System.currentTimeMillis();

				worker.decrypt(files);

				long endTime = System.currentTimeMillis();

				System.out.println("That took " + (endTime - startTime) + " milliseconds ... Kiss goodbye !");
			} else {
				System.out.println("Unknown command " + args[0]);
				System.exit(0);
			}

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
