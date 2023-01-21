package com.x7th.sole;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	ApplicationContext context;

	@RequestMapping(value = "/**", method = RequestMethod.GET)
	public String anyGet(HttpServletRequest req, HttpServletResponse res, Model model, RedirectAttributes attr) throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException, AnonymousException, ForbiddenException {
		File file = new File("build/resources/main/static", req.getRequestURI());
		if (file.isFile()) {
			FileInputStream in = new FileInputStream(file);
			in.transferTo(res.getOutputStream());
			return null;
		}
		return any("Get", req, res, model, attr);
	}

	@RequestMapping(value = "/**", method = RequestMethod.POST)
	public String anyPost(HttpServletRequest req, HttpServletResponse res, Model model, RedirectAttributes attr) throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException, AnonymousException, ForbiddenException {
		return any("Post", req, res, model, attr);
	}

	public String any(String name, HttpServletRequest req, HttpServletResponse res, Model model, RedirectAttributes attr) throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException, AnonymousException, ForbiddenException {

		String p = "com/x7th/sole";

		ClassLoader loader = getClass().getClassLoader();
		LinkedList<Class<?>> pathParamTypes = new LinkedList<Class<?>>();
		LinkedList<Object> pathParams = new LinkedList<Object>();
		for (String s: req.getRequestURI().split("/")) {
			if (0 < s.length()) {
				if (loader.getResource(p + "/" + s) != null) {
					p += "/" + s;
				} else if (loader.getResource(p + "/__") != null) {
					p += "/__";
					pathParamTypes.add(String.class);
					pathParams.add(s);
				} else {
					throw new NotFoundException();
				}
			}
		}

		try {
			ClassLoader classLoader = this.getClass().getClassLoader();
			Class<?> c1 = loader.loadClass(p.replace('/', '.') + ".Holder");
			Object ctrl = context.getBean(c1);

			Class<?> c2 = classLoader.loadClass(p.replace('/', '.') + ".Holder$" + name);
			Constructor constructor = c2.getDeclaredConstructor(new Class<?>[]{c1});
			Object o = constructor.newInstance(new Object[]{ctrl});
			for (java.lang.reflect.Field f: c2.getFields()) {
				if (f.getGenericType() == Model.class) {
						f.set(o, model);
				} else if (f.getGenericType() == RedirectAttributes.class) {
						f.set(o, attr);
				} else if (f.getDeclaredAnnotation(Param.class) != null) {
					String[] v = req.getParameterMap().get(f.getName());
					if (v != null) {
						if (f.getGenericType() == boolean.class) {
							f.set(o, "on".equals(v[0])); // TBD always "on"?
						} else if (f.getGenericType() == int.class) {
							f.set(o, Integer.parseInt(v[0]));
						} else if (f.getGenericType() == long.class) {
							f.set(o, Long.parseLong(v[0]));
						} else if (f.getGenericType() == String.class) {
							f.set(o, v[0]);
						}
					}
				}
			}
			Method method = c2.getDeclaredMethod("exe", pathParamTypes.toArray(new Class<?>[]{}));
			return (String) method.invoke(o, pathParams.toArray());
		} catch (ClassNotFoundException ex) {
			throw new NotFoundException();
		} catch (NoSuchMethodException ex) {
			throw new NotFoundException();
		} catch (InvocationTargetException ex) {
			//throw ex.getTargetException();
			if (ex.getTargetException() instanceof NotFoundException x) {
				throw x;
			}
			if (ex.getTargetException() instanceof AnonymousException x) {
				throw x;
			}
			if (ex.getTargetException() instanceof ForbiddenException x) {
				throw x;
			}
			throw ex;
		}
	}
}
