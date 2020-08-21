package utils;

import java.util.Arrays;
import lombok.SneakyThrows;
import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;
import report.custom.TestReporting;
import test.DriverFactory;

public class CustomizeAssert extends Assertion {

	TestReporting reporting = DriverFactory.getTestReporting();
	private String getValues(Object data){
		if(data.getClass().isArray()){
        	return Arrays.deepToString((Object[]) data);
		}
		else{
			return String.valueOf(data);
		}
	}

	@SneakyThrows
	@Override
	public void onAssertSuccess(IAssert assertCommand) {
		String message = "Assertion Result";
		if(assertCommand.getMessage() != null) {
			message = assertCommand.getMessage();
		}
		String expectedMessage = assertCommand.getExpected() == null ? "NULL" : getValues(assertCommand.getExpected());
		String actualMessage =  assertCommand.getActual() == null ? "NULL" : getValues(assertCommand.getActual());
		reporting.log(message,"Expected: "+expectedMessage+" Actual: "+actualMessage, "Pass");
	}
	 
	@SneakyThrows
	@Override
	public void onAssertFailure(IAssert assertCommand, AssertionError ex) {
		String message = "Assertion Result";
		if(assertCommand.getMessage() != null) {
			message = assertCommand.getMessage();
		}
		String expectedMessage = assertCommand.getExpected() == null ? "NULL" : getValues(assertCommand.getExpected());
		String actualMessage =  assertCommand.getActual() == null ? "NULL" : getValues(assertCommand.getActual());
		if(ex.getCause() == null){
			reporting.log(message,"Expected: "+expectedMessage+" Actual"+actualMessage + "<Br/>" + ex.toString(), "Fail");
		}
		else{
			reporting.log(message,"Expected: "+expectedMessage+" Actual"+actualMessage + "<Br/>" + ex.toString(), "Fail");
		}
	}

//	@Override
	@SneakyThrows
	public void onAssertFailure(IAssert assertCommand) {
		String message = "Assertion Result";
		if(assertCommand.getMessage() != null) {
			message = assertCommand.getMessage();
		}
		String expectedMessage = assertCommand.getExpected() == null ? "NULL" : getValues(assertCommand.getExpected());
		String actualMessage =  assertCommand.getActual() == null ? "NULL" : getValues(assertCommand.getActual());
		reporting.log(message,"Expected: "+expectedMessage+" Actual: "+actualMessage, "Pass");
	}
}
