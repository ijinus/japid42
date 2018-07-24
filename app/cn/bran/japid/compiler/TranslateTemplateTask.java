/**

 * Copyright 2010 Bing Ran<bing_ran@hotmail.com> 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package cn.bran.japid.compiler;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.bran.japid.classmeta.AbstractTemplateClassMetaData;
import cn.bran.japid.util.DirUtil;
import cn.bran.japid.util.JapidFlags;

/**
 * modeled after the JamonTask in the <a url = "http://www.jamon.org/>Jamon
 * project</a>
 * 
 * ensure you have the Japid task properly defined,
 * 
 * <pre>
 * <path id="JapidCP">
 *     <pathelement file="/path/to/japid.jar"/>
 *   </path>
 *   <taskdef name="japid"
 *            classname="bran.ant.TranslateTemplateTask"
 *            classpathref="${JapidCP}"/>
 * 
 * To generate the Java sources, include the following target:
 * 
 *   <target name="trans-templates">
 *     <japid srcdir="templates" destdir="tempsrc" />
 *   </target>
 * </pre>
 * 
 * @author Bing Ran<bing_ran@hotmail.com>
 * 
 */
public class TranslateTemplateTask {
	
	private List<Class<?>> staticImports = new ArrayList<Class<?>>();
	private List<String> imports = new ArrayList<String>();
	// changed html source files
	private List<File> changedFiles;
	// updated Java files
	private List<File> changedTargetFiles = new LinkedList<File>();
	public List<File> getChangedTargetFiles() {
		return this.changedTargetFiles;
	}

	private boolean usePlay = true;

//	public boolean isUsePlay() {
//		return usePlay;
//	}

	public void setUsePlay(boolean _usePlay) {
		this.usePlay = _usePlay;
	}

	/**
	 * get a list of changed files in the last task execution
	 * 
	 * @return
	 */
	public List<File> getChangedFiles() {
		return this.changedFiles;
	}

	public void setDestdir(File p_destDir) {
		this.destDir = p_destDir;
	}

	public void setPackageRoot(File p_srcDir) {
		this.packageRoot = p_srcDir;
	}

	public void setListFiles(boolean p_listFiles) {
		this.listFiles = p_listFiles;
	}

	public void execute(){
		// first off, make sure that we've got a srcdir

		if (this.packageRoot == null) {
			throw new RuntimeException("srcdir attribute must be set!");
		}
		if (this.destDir == null) {
			this.destDir = this.packageRoot;
		}

		if (!this.packageRoot.exists() || !this.packageRoot.isDirectory()) {
			throw new RuntimeException("source directory \"" + this.packageRoot + "\" does not exist or is not a directory");
		}

		this.destDir.mkdirs();
		if (!this.destDir.exists() || !this.destDir.isDirectory()) {
			throw new RuntimeException("destination directory \"" + this.destDir + "\" does not exist or is not a directory");
		}

		if(this.changedFiles == null) 
			this.changedFiles = DirUtil.findChangedSrcFiles(this.include);

		if (this.changedFiles.size() > 0) {
//			JapidFlags.log("[Japid] Processing " + changedFiles.size() + " template" + (changedFiles.size() == 1 ? "" : "s") + " in directory tree: " + destDir);

			JapidTemplateTransformer tran = new JapidTemplateTransformer(this.packageRoot.getPath(), null);
			tran.usePlay(this.usePlay);
			for (Class<?> c : this.staticImports) {
				JapidTemplateTransformer.addImportStatic(c);
			}
			for (String c : this.imports) {
				JapidTemplateTransformer.addImportLine(c);
			}
			for (Class<? extends Annotation> a : this.typeAnnotations) {
				JapidTemplateTransformer.addAnnotation(a);
			}

			for (int i = 0; i < this.changedFiles.size(); i++) {
				File templateFile = this.changedFiles.get(i);
				JapidFlags.log("[Japid] Transforming template: " + templateFile.getPath() + " to: " + DirUtil.mapSrcToJava(templateFile.getName()));
				if (this.listFiles) {
					JapidFlags.log(templateFile.getAbsolutePath());
				}

				try {
					String relativePath = DirUtil.getRelativePath(templateFile, this.packageRoot);
					File generate = tran.generate(relativePath);
					this.changedTargetFiles.add(generate);
				} catch (JapidCompilationException e) {
					// syntax error
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getClass().getName() + ":" + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * derived artifacts are kept in memory instead of file system
	 * 
	 * @author Bing Ran (bing.ran@hotmail.com)
	 */
	public void executeInMemory(){
		// first off, make sure that we've got a srcdir

		if (this.packageRoot == null) {
			throw new RuntimeException("srcdir attribute must be set!");
		}
		
		if (!this.packageRoot.exists() && !this.packageRoot.isDirectory()) {
			throw new RuntimeException("source directory \"" + this.packageRoot + "\" does not exist or is not a directory");
		}

		if(this.changedFiles == null) 
			this.changedFiles = DirUtil.findChangedSrcFiles(this.include);

		if (this.changedFiles.size() > 0) {
//			JapidFlags.log("[Japid] Processing " + changedFiles.size() + " template" + (changedFiles.size() == 1 ? "" : "s") + " in directory tree: " + destDir);

			JapidTemplateTransformer tran = new JapidTemplateTransformer(this.packageRoot.getPath(), null);
			tran.usePlay(this.usePlay);
			for (Class<?> c : this.staticImports) {
				JapidTemplateTransformer.addImportStatic(c);
			}
			for (String c : this.imports) {
				JapidTemplateTransformer.addImportLine(c);
			}
			for (Class<? extends Annotation> a : this.typeAnnotations) {
				JapidTemplateTransformer.addAnnotation(a);
			}

			for (int i = 0; i < this.changedFiles.size(); i++) {
				File templateFile = this.changedFiles.get(i);
				JapidFlags.log("[Japid] Transforming template: " + templateFile.getPath() + " to: " + DirUtil.mapSrcToJava(templateFile.getName()));
				if (this.listFiles) {
					JapidFlags.log(templateFile.getAbsolutePath());
				}

				try {
					String relativePath = DirUtil.getRelativePath(templateFile, this.packageRoot);
					File generate = tran.generate(relativePath);
					this.changedTargetFiles.add(generate);
				} catch (JapidCompilationException e) {
					// syntax error
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getClass().getName() + ":" + e.getMessage());
				}
			}
		}
	}

	/**
	 * add an import static entry to the template translator
	 * 
	 * @param clz
	 */
	public void importStatic(Class<?> clz) {
		this.staticImports.add(clz);
	}

	/**
	 * 
	 * @param imp
	 *            the package or class part of a full line of regular imports,
	 *            such as java.util.*, java.util.Hashmap
	 */
	public void addImport(String imp) {
		this.imports.add(imp);
	}

	public void addImport(Class<?> clz) {
		this.imports.add(clz.getName().replace('$', '.'));
	}

//	private static class JapidFileNameMapper {
//
//		static String mapFileName(String sourceName) {
//			String targetFileName = sourceName;
//			String suffix = ".html";
//			if (targetFileName.endsWith(suffix)) {
//				targetFileName = targetFileName.substring(0, targetFileName.length() - suffix.length());
//				return targetFileName + ".java" ;
//			} else {
//				return null;
//			}
//			// return new String[0];
//		}
//	}

	private File destDir = null;
	private File packageRoot = null;
	private boolean listFiles = false;

	public void addAnnotation(Class<? extends Annotation> anno) {
		this.typeAnnotations.add(anno);
	}

	List<Class<? extends Annotation>> typeAnnotations = new ArrayList<Class<? extends Annotation>>();
	private File include;

	// private ClassLoader m_classLoader = JamonTask.class.getClassLoader();
	public void setUseStreaming(boolean streaming) {
		AbstractTemplateClassMetaData.streaming = streaming;
	}

	/**
	 * 
	 * @param file the sub dir in the root that contains the template tree
	 */
	public void setInclude(File file) {
		this.include = file;
	}

	public void clearImports() {
		this.staticImports.clear();
		this.imports.clear();
		
		AbstractTemplateClassMetaData.clearImports();
	}

	/**
	 * @param changedFiles the changedFiles to set. Each file starts with the package root. 
	 */
	public void setChangedFiles(List<File> _changedFiles) {
		this.changedFiles = _changedFiles;
	}
}
