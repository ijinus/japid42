/**
 * 
 */
package cn.bran.play;

import java.util.Map;

import cn.bran.japid.template.AuthenticityCheck;
import cn.bran.japid.util.StringUtils;

import play.data.Form;
import play.data.format.Formatters;
import play.data.validation.ValidationError;
import play.i18n.MessagesApi;

import static play.libs.F.*;
import play.mvc.Http.Session;
import java.util.*;
import play.libs.crypto.CookieSigner;

/**
 * add authenticity token check in form binding
 * 
 * @author bran
 * 
 */
public class AuthenticForm<T> extends Form<T> {
	private Class<T> formType;


    private static play.api.inject.Injector injector() {
        return play.api.Play.current().injector();
    }
	
	public AuthenticForm(Class<T> clazz) {
		this(null, clazz);
	}

	public AuthenticForm(String name, Class<T> clazz) {
		this(name, clazz, new HashMap<String,String>(), new HashMap<String,List<ValidationError>>(), Optional.empty(),  null);
	}

	public AuthenticForm(String name, Class<T> clazz, Class<?> groups) {
		this(name, clazz, new HashMap<String,String>(), new HashMap<String,List<ValidationError>>(), Optional.empty(), groups);
	}

	public AuthenticForm(String rootName, Class<T> clazz, Map<String,String> data, Map<String,List<ValidationError>> errors, Optional<T> value) {
		this(rootName, clazz, data, errors, value, null);
	}

	public AuthenticForm(String rootName, Class<T> clazz, Map<String,String> data, Map<String,List<ValidationError>> errors, Optional<T> value, Class<?> groups) {
		super(rootName, clazz, data, errors, value, groups,injector().instanceOf(MessagesApi.class), injector().instanceOf(Formatters.class), injector().instanceOf(javax.validation.Validator.class));
		this.formType = clazz;
	}

	private void checkAuthenticity(Map<String, String> data) {
		AuthenticityCheck anno = this.formType.getAnnotation(AuthenticityCheck.class);
		Session session = play.mvc.Http.Context.current().session();
		String atoken = session.get(AuthenticityCheck.AUTH_TOKEN);
		session.remove(AuthenticityCheck.AUTH_TOKEN);
		String uuid = data.get(AuthenticityCheck.AUTH_TOKEN);

		if (anno != null) {
			if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(atoken)) {
				alarm();
			} else {
				match(atoken, uuid);
			}
		}
	}

	private void match(String atoken, String uuid) {
//		String sign = Crypto.sign(uuid);
		String sign = play.Play.application().injector().instanceOf(CookieSigner.class).sign(uuid);
		if (!sign.equals(atoken)) {
			alarm();
		}
	}

	private void alarm() {
		throw new SecurityException("The form submission does not have a proper authenticity token");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.data.Form#bind(java.util.Map, java.lang.String[])
	 */
	@Override
	public Form<T> bind(Map<String, String> data, String... allowedFields) {
		checkAuthenticity(data);
		return super.bind(data, allowedFields);
	}
}
