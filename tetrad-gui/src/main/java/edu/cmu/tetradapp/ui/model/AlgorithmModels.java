/*
 * Copyright (C) 2017 University of Pittsburgh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package edu.cmu.tetradapp.ui.model;

import edu.cmu.tetrad.annotation.AlgType;
import edu.cmu.tetrad.annotation.Algorithm;
import edu.cmu.tetrad.annotation.AlgorithmAnnotations;
import edu.cmu.tetrad.annotation.AnnotatedClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;

/**
 *
 * Nov 30, 2017 4:20:43 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class AlgorithmModels {

	private static final AlgorithmModels INSTANCE = new AlgorithmModels();

	private final List<AlgorithmModel> models;
	private final Map<AlgType, List<AlgorithmModel>> modelMap;
	// create the plugin manager
	private final PluginManager pluginManager = new DefaultPluginManager();

	private AlgorithmModels() {
		// load the plugins
		pluginManager.loadPlugins();

		// start (active/resolved) the plugins
		pluginManager.startPlugins();

		AlgorithmAnnotations algoAnno = AlgorithmAnnotations.getInstance();
		List<AlgorithmModel> list = algoAnno.filterOutExperimental(algoAnno.getAnnotatedClasses())
				.stream()
				.map(e -> new AlgorithmModel(e))
				.sorted()
				.collect(Collectors.toList());

		// Plugin Algorithm
		List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
		for (PluginWrapper plugin : startedPlugins) {
			String pluginId = plugin.getDescriptor().getPluginId();
			System.out.println("PluginId: " + pluginId);
			List extensions = pluginManager.getExtensions(pluginId);
			for (Object extension : extensions) {

				Class clazz = extension.getClass();
				
				System.out.println("Algorithm Extension CanonicalName: " + clazz.getCanonicalName());
				System.out.println("Algorithm Extension SimpleName: " + clazz.getSimpleName());
				try {
					System.out.println("Class Name: " + clazz.getName());
					AnnotatedType[] annotatedTypes = clazz.getAnnotatedInterfaces();
					if(annotatedTypes != null) {
						for(int i=0;i<annotatedTypes.length;i++) {
							AnnotatedType annotatedType = annotatedTypes[i];
							System.out.println("AnnotatedType: " + annotatedType.getType());
						}
					}
					Annotation[] annotations = clazz.getDeclaredAnnotations();
					if(annotations != null) {
						for(int i=0;i<annotations.length;i++) {
							Annotation annotation = annotations[i];
							System.out.println("Annotation: " + annotation.toString());
							System.out.println("Annotation.getClass: " + annotation.getClass());
							System.out.println("Annotation.annotationType: " + annotation.annotationType());
							
							/*if(annotation.toString().contains("edu.cmu.tetrad.annotation.Algorithm")) {
								AnnotatedClass<Algorithm> annotatedAlgor = new AnnotatedClass<Algorithm>(clazz, (Algorithm) annotation);
								AlgorithmModel algorPlugin = new AlgorithmModel(annotatedAlgor);
								list.add(algorPlugin);
							}*/
						}
					}
					
					Method[] mothods = clazz.getDeclaredMethods();
					if(mothods != null) {
						for(int i=0;i<mothods.length;i++) {
							Method method = mothods[i];
							System.out.println("Method: " + method.getName());
							Type[] types = method.getGenericParameterTypes();
							if(types != null) {
								for(int j=0;j<types.length;j++) {
									System.out.println("Method.getGenericParameterType: " + types[j].getTypeName());
								}
							}
							System.out.println("Method.getGenericReturnType: " + method.getGenericReturnType());
						}
					}
					
					
					
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		models = Collections.unmodifiableList(list);

		Map<AlgType, List<AlgorithmModel>> map = new EnumMap<>(AlgType.class);

		// initialize enum map
		for (AlgType algType : AlgType.values()) {
			map.put(algType, new LinkedList<>());
		}

		// group by datatype
		models.stream().forEach(e -> {
			map.get(e.getAlgorithm().getAnnotation().algoType()).add(e);
		});

		// make it unmodifiable
		map.forEach((k, v) -> map.put(k, Collections.unmodifiableList(v)));
		modelMap = Collections.unmodifiableMap(map);
	}

	public static AlgorithmModels getInstance() {
		return INSTANCE;
	}

	public List<AlgorithmModel> getModels() {
		return models;
	}

	public List<AlgorithmModel> getModels(AlgType algType) {
		return modelMap.containsKey(algType) ? modelMap.get(algType) : Collections.EMPTY_LIST;
	}

}
