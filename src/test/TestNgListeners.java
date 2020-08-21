package test;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestNgListeners implements ITestListener, IExecutionListener {


  // Before the suite start
  @Override
  public void onExecutionStart() {
    System.out.println("Before the suite");
  }


  // When Test case get failed, this method is called.
  @Override
  public void onTestFailure(ITestResult Result) {
    System.out.println("The name of the testcase failed is :" + Result.getName());
  }

  // When Test case get Skipped, this method is called.
  @Override
  public void onTestSkipped(ITestResult Result) {
    System.out.println("The name of the testcase Skipped is :" + Result.getName());
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

  }

  @Override
  public void onStart(ITestContext iTestContext) {

  }

  @Override
  public void onFinish(ITestContext iTestContext) {

  }

  // When Test case get Started, this method is called.
  @Override
  public void onTestStart(ITestResult Result) {
    System.out.println(Result.getName() + " test case started");
  }

  // When Test case get passed, this method is called.
  @Override
  public void onTestSuccess(ITestResult Result) {
    System.out.println("The name of the testcase passed is :" + Result.getName());
  }

  // After the suite finish
  @Override
  public void onExecutionFinish() {
    System.out.println("After the suite");
  }


}
