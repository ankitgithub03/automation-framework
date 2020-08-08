package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EmptyStackException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.Constants;

public class ExecuteCommand {

  static Logger log = LoggerFactory.getLogger(ExecuteCommand.class);


  /**
   *
   * @param commandQuery   -- command to hit on the console of OS
   * @param counter    -- number of counters for retry to hit the command
   * @param printToConsole  -- print to the command output on the console using println (flag -- true, false)
   * @param waitFor   -- wait for command output (flag -- true, false)
   * @param commandWaitTimeout  -- wait time for command output
   *
   * @return  it will return the three values in the object,
   *          1st - output
   *          2nd - exit value
   */
  public Object[] executeCommand(String commandQuery, int counter, boolean printToConsole, boolean waitFor, long... commandWaitTimeout){
    long waitTime = commandWaitTimeout.length > 0 ? commandWaitTimeout[0] : (10 * 1000);
    if(commandQuery.startsWith("adb")){
      commandQuery = Constants.ADB_PATH+commandQuery;
    }
    String output = "";
    int exitValue = -1;
    try{
      CommandLine command = new CommandLine(OSValidator.shellType);
      if(OSValidator.shellType.trim().equalsIgnoreCase("cmd"))
        command.addArgument("/c", false);
      else{
        command.addArgument("-l", false);
        command.addArgument("-c", false);
      }
      command.addArgument(commandQuery, false);

      ByteArrayOutputStream stdout = new ByteArrayOutputStream();
      PumpStreamHandler psh = new PumpStreamHandler(stdout);
      DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
      DefaultExecutor executor = new DefaultExecutor();
      executor.setStreamHandler(psh);
      while(counter >0) {
        try {
          executor.execute(command, resultHandler);
          if (waitFor)
            resultHandler.waitFor(waitTime);
          exitValue = resultHandler.getExitValue();
          if (printToConsole)
            System.out.println(stdout);
          output = stdout.toString();
          if(output.isEmpty()){
            throw new Exception("command output is empty");
          }
        } catch (IOException | InterruptedException e1) {
          log.info("Threw a Exception in BaseUtil::runtimeCommand, full stack trace follows:", e1);
        }
        catch (Exception e){
          System.err.println(e.getMessage());
          log.info("runtimeCommand, full stack trace follows: ", e.getMessage());
        }
        finally{
          counter--;
          JavaWrappers.sleep(1);
        }
      }
    }
    catch(Exception ex){
      //Do Nothing
    }
    return new Object[]{output, exitValue};
  }

}
