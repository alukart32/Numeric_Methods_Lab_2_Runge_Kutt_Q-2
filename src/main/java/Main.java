import data.FileData;
import rungeKutt.RungeMethod;

public class Main {
    public static void main(String ...args){
        FileData fileData = new FileData();
        RungeMethod rungeMethod = new RungeMethod(fileData);
        rungeMethod.solve();
    }
}
