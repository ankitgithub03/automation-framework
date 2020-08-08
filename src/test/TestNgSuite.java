package test;

import java.io.File;
import java.io.IOException;
import org.testng.ITestContext;
import report.custom.FinalReport;

public interface TestNgSuite {

  FinalReport finalReport = new FinalReport();
  String projDir = System.getProperty("user.dir") + File.separator + "src";

  void initializeSuite(ITestContext itx) throws IOException;

  void tearDownSuite();

}
