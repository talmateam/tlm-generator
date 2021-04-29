package com.everis.microgenerator.service;

import com.everis.microgenerator.model.Repo;
import com.everis.microgenerator.model.Template;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class TemplateGenerator {

	public ArrayList<Repo> getRepository(){
		ArrayList<Repo> repositories = new ArrayList<>();
		Properties prop = new Properties();
		InputStream inputStream = null;

		try {
			File file = ResourceUtils.getFile("classpath:config/repository.properties");
			inputStream =  new FileInputStream(file != null?file.getPath():"/config/repository.properties");
			prop.load(inputStream);
		} catch(IOException e) {
			System.out.println(e.toString());
		}

		for(Enumeration<Object> e = prop.keys(); e.hasMoreElements();) {
			Object obj = e.nextElement();
			String idRepository = obj.toString().split("-")[0];
			String nombreRepository = obj.toString().split("-")[1].replace("_", " ");
			String urlRepository = prop.getProperty(obj.toString());
			repositories.add(new Repo(idRepository, nombreRepository, urlRepository));
		}

		return repositories;
	}

	public Template getParams(Repo repo){
		Map<String, String> map = new HashMap<>();
		Properties prop = new Properties();
		InputStream inputStream = null;

		if(!repo.id.equals("0")){
			String gitRepository = repo.url.toString().split("bitbucket.org/")[1].replace(".git", "");

			try {
				URL url = new URL("https://api.bitbucket.org/2.0/repositories/"+ gitRepository +"/src/master/src/main/g8/default.properties");
				URLConnection urlCon = url.openConnection();
				inputStream = urlCon.getInputStream();
				prop.load(inputStream);
				for(Enumeration<Object> e = prop.keys(); e.hasMoreElements();) {
					Object obj = e.nextElement();
					String idRepository = obj.toString().split("-")[0];
					String nameParam = obj.toString().split("=")[0];
					String valueDefault = prop.getProperty(obj.toString());
					map.put(nameParam, valueDefault);
				}
			} catch(IOException e) {
				System.out.println(e.toString());
			}
		}

		return new Template();
	}

	public ArrayList<Repo> getDependencias(Repo repo){

		ArrayList<Repo> rp = new ArrayList<Repo>();
		Properties prop = new Properties();
		InputStream inputStream = null;
			String gitRepository = repo.url.toString().split("bitbucket.org/")[1].replace(".git", "");
			try {
				URL url = new URL("https://api.bitbucket.org/2.0/repositories/"+ gitRepository +"/src/master/src/main/g8/default.properties");
				URLConnection urlCon = url.openConnection();
				inputStream = urlCon.getInputStream();
				prop.load(inputStream);
				for(Enumeration<Object> e = prop.keys(); e.hasMoreElements();) {
					Object obj = e.nextElement();
					String nameParam = obj.toString().split("=")[0];
					String[] dp = nameParam.split("_");
					String v="dp";
					if (v.equals(dp[0])){
						Repo r=new Repo();
						r.id = nameParam.toString();
						r.val=prop.getProperty(obj.toString());
						rp.add(r);
					}
				}
			} catch(IOException e) {
				System.out.println(e.toString());
			}
		return rp;
	}

	public byte[] getTemplate(ArrayList<String> params, String name) throws Exception {
		log.info("params: {}", String.join(" ", params));

		String randomName = String.format("%tY%<tm%<td_%<tH%<tM%<tS", System.currentTimeMillis()) + "_"
				+ (int) (Math.random() * 100 + 1);
		String directoryName = "src/main/java/com/everis/microgenerator/temp/" + randomName;
		log.info("directoryName: {}", directoryName);
		Path tempDirectoryBase = Paths.get(directoryName);
		if (!tempDirectoryBase.toFile().exists()) {
			Files.createDirectories(tempDirectoryBase);
		}
		File outputFile = new File(directoryName + "/log.txt");

		ProcessBuilder builder = new ProcessBuilder(params);

		builder.redirectErrorStream(true);
		builder.redirectOutput(outputFile);
		val directory = new File(directoryName);
		builder.directory(directory);
		Process process = builder.start();
		int exitStatus = process.waitFor();
		val targets = directory.listFiles((dir, filename) -> "target".equals(filename));
		if (targets.length > 0) {
			val target = targets[0];
			Files
					.walk(target.toPath())
					.sorted(Comparator.reverseOrder())
					.map(path -> path.toFile())
					.forEach(file -> file.delete());
		}

		zipGenerate(directoryName, directoryName+".zip");
		byte[] result = getBytes(directoryName+".zip");
		process.destroyForcibly();
		File folder= new File(directoryName);
		boolean status = deleteDirectory(folder);
		return result;
	}

	private void zipGenerate(String origen, String destino) throws Exception {
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(destino));
		zipAddFolder("", origen, zip);
		zip.flush();
		zip.close();
	}

	private void zipAddFile(String path, String fichero, ZipOutputStream zip) throws Exception {

		File archivo = new File(fichero);
		if (archivo.isDirectory()) {
			zipAddFolder(path, fichero, zip);
		} else {
			byte[] buffer = new byte[4096];
			int leido;
			FileInputStream entrada = new FileInputStream(archivo);
			zip.putNextEntry(new ZipEntry(path + "/" + archivo.getName()));
			while ((leido = entrada.read(buffer)) > 0) {
				zip.write(buffer, 0, leido);
			}
		}
	}

	private void zipAddFolder(String path, String carpeta, ZipOutputStream zip) throws Exception {
		File directorio = new File(carpeta);

		for (String nombre_archivo : directorio.list()) {
			if (path.equals("")) {
				zipAddFile(directorio.getName(), carpeta + "/" + nombre_archivo, zip);
			} else {
				zipAddFile(path + "/" + directorio.getName(), carpeta + "/" + nombre_archivo, zip);
			}
		}
	}

	private byte[] getBytes(String zipPath) {

		FileInputStream fileInputStream = null;
		File zipFile = new File(zipPath);
		byte[] zipByteStream = new byte[(int) zipFile.length()];
		try {
			fileInputStream = new FileInputStream(zipFile);
			fileInputStream.read(zipByteStream);
			fileInputStream.close();
		} catch (Exception e) {
			System.out.println("Exception" + e);
		}
		return zipByteStream;
	}

	boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}
}
