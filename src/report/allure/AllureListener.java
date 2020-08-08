package report.allure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import io.qameta.allure.Attachment;

public class AllureListener implements ITestListener {

	private static String getTestMethodName(ITestResult iTestResult) {
		return iTestResult.getMethod().getConstructorOrMethod().getName();
	}
	
	@Attachment
	public byte[] saveFailureScreenShot(WebDriver driver) {
		return ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
	}
	
	@Attachment(value = "{0}", type = "text/plain")
	public static String saveTextLog(String message) {
		return message;
	}
	
	@Attachment(value = "video", type = "video/quicktime")
    public byte[] attachment() throws Exception {
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
//		MyScreenRecorder.startRecording(dateFormat.format(new Date()));
        try {
            File video = MyScreenRecorder.screenRecorder.getCreatedMovieFiles().get(0);
//            await().atMost(5, TimeUnit.SECONDS)
//                    .pollDelay(1, TimeUnit.SECONDS)
//                    .ignoreExceptions()
//                    .until(() -> video != null);
            return Files.readAllBytes(Paths.get(video.getAbsolutePath()));
        } catch (IOException e) {
            return new byte[0];
        }
    }
	
//	@Attachment
//	public File saveVideo() throws SecurityException, Exception {
//		return MyScreenRecorder.screenRecorder.getCreatedMovieFiles().get(0);
//		//return new Tests() {}.getClass().getEnclosingMethod().getName();		
//	}
		
	
	@Override
	public void onStart(ITestContext iTestContext) {
		System.out.println("I am in onStart method " + iTestContext.getName());
//		iTestContext.setAttribute("WebDriver", BaseClass.getDriver());
	}

	@Override
	public void onFinish(ITestContext iTestContext) {
		System.out.println("I am in onFinish method " + iTestContext.getName());
	}

	@Override
	public void onTestStart(ITestResult iTestResult) {
		System.out.println("I am in onTestStart method " + getTestMethodName(iTestResult) + " start");
	}

	@Override
	public void onTestSuccess(ITestResult iTestResult) {
		System.out.println("I am in onTestSuccess method " + getTestMethodName(iTestResult) + " succeed");
		try {
			attachment();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestFailure(ITestResult iTestResult) {
		System.out.println("I am in onTestFailure method " + getTestMethodName(iTestResult) + " failed");
//		WebDriver driver = BaseClass.getDriver();
//		// Allure ScreenShot and SaveTestLog
//		if (driver instanceof WebDriver) {
//			System.out.println("Screenshot captured for test case:" + getTestMethodName(iTestResult));
//			saveFailureScreenShot(driver);
//		}
//		saveTextLog(getTestMethodName(iTestResult) + " failed and screenshot taken!");	
//		try {
//			attachment();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	
	@Override
	public void onTestSkipped(ITestResult iTestResult) {
		System.out.println("I am in onTestSkipped method " + getTestMethodName(iTestResult) + " skipped");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
		System.out.println("Test failed but it is in defined success ratio " + getTestMethodName(iTestResult));
	}

}
