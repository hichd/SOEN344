package junit.runner;

import java.lang.reflect.Method;

import junit.framework.Test;
import junit.framework.TestSuite;

public abstract class BaseTestRunner {

	protected abstract Class loadSuiteClass(String testCase) throws ClassNotFoundException;

	protected abstract void runFailed(String message);

	protected abstract void clearStatus();

	protected abstract String invalidClassNameMessage();

	private static final String SUITE_METHODNAME = "suite";

	public Test getTest(String suiteClassName) {
		if (suiteClassName.length() <= 0) {
			clearStatus();
			runFailed(invalidClassNameMessage());
			return null;
		}
		
		Class testClass= null;
		try {
			 testClass= loadSuiteClass(suiteClassName);
		} catch(Exception e) {
			runFailed("Class \""+suiteClassName+"\" not found");
			return null;
		}
		
		Method suiteMethod= null;
		try {
			suiteMethod= testClass.getMethod(SUITE_METHODNAME, new Class[0]);
		} catch(Exception e) {
			clearStatus();
			return new TestSuite(testClass);
		}
		
		Test test= null;
		try {
			test= (Test)suiteMethod.invoke(null, new Class[0]); // static method
		} catch(Exception e) {
			runFailed("Could not invoke the suite() method");
			return null;
		}
		clearStatus();
		return test;
	}

}
