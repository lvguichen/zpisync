package zpisync.desktop;

import javax.swing.*;

import com.sun.xml.internal.txw2.IllegalSignatureException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Dispatches incoming calls onto EDT.
 */
public class EdtProxy {
	/**
	 * Non-blocking version of "waiting". Calling on proxy methods will always
	 * get null. Nevertheless, execution of delegate's methods is schdeduled to
	 * EDT for late.
	 * 
	 * @param delegate
	 * @return proxy
	 */
	@SuppressWarnings("unchecked")
	public static <T> T async(final T delegate, Class<?>... interfaces) {
		InvocationHandler handler = new InvocationHandler() {
			public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable {
				Call call = new Call(method, delegate, args);
				if (SwingUtilities.isEventDispatchThread())
					call.run();
				else
					SwingUtilities.invokeLater(call);
				return null;
			}
		};
		return (T) createProxy(delegate, interfaces, handler);
	}

	/**
	 * Creates a proxy which implements all interfaces of the delegate and that
	 * transforms all calls for delegate to be executed in EDT.
	 * 
	 * During execution, the caller thread is blocked. The result returned by
	 * delegate's methods is awaited and returned to caller thread.
	 * 
	 * @param delegate
	 * @return proxy
	 */
	@SuppressWarnings("unchecked")
	public static <T> T blocking(final T delegate, Class<?>... interfaces) {
		InvocationHandler handler = new InvocationHandler() {
			public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
				Call call = new Call(method, delegate, args);
				if (SwingUtilities.isEventDispatchThread())
					call.run();
				else
					SwingUtilities.invokeAndWait(call);
				if (call.getError() != null)
					throw call.getError();
				return call.getResult();
			}
		};
		return (T) createProxy(delegate, interfaces, handler);
	}

	private EdtProxy() {
	}

	private static Object createProxy(Object delegate, Class<?>[] interfaces, InvocationHandler handler) {
		return Proxy.newProxyInstance(EdtProxy.class.getClassLoader(), interfaces, handler);
	}

	private static class Call implements Runnable {
		private final Method method;
		private final Object delegate;
		private final Object[] args;

		private Object result;
		private Throwable error;

		public Call(Method method, Object delegate, Object[] args) {
			this.method = method;
			this.delegate = delegate;
			this.args = args;
		}

		public void run() {
			try {
				result = method.invoke(delegate, args);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException("Could not call delegate", e);
			} catch (InvocationTargetException e) {
				throw new IllegalSignatureException("Could not call delegate", e);
			} catch (Throwable e) {
				error = e;
			}
		}

		public Object getResult() {
			return result;
		}

		public Throwable getError() {
			return error;
		}
	}
}
