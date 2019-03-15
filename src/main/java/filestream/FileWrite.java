package filestream;
import java.io.*;

public class FileWrite {
    String space;
    // pathname of output files
    private String filepath = "E:\\Programming\\Numeric_Method_Runge_Kutt_2_Method\\src\\main\\resources\\output.txt";
    PrintWriter pw = new PrintWriter(System.out, true);

    public void write(int precision, double ...tmp){
        File fout = new File(filepath);
        try(PrintWriter file = new PrintWriter( new FileWriter(filepath, true))){

            String template = "%1$." + Integer.toString(precision) + "f";

            if(tmp.length < 0 || tmp.length > 4){
                file.print("Лесом");
            }else {
                file.println();
                file.printf(template,tmp[0]);
                file.print("  |  ");

                file.printf(template,tmp[1]);
                file.print("  |  ");

                file.printf(template,tmp[2]);

                if(tmp.length == 4){
                    file.print("  |  ");
                    file.print(tmp[3]);
                }
            }
            file.println();
            file.print("----------------------------------------------------------------------------------------------------");
        }catch (IOException exp){
            pw.println("Error!!!");
            pw.println("File writing handling problem!");
            exp.printStackTrace();
        }
    }

    public void write(double tmp){
        File fout = new File(filepath);
        try(BufferedWriter file = new BufferedWriter(new FileWriter(fout, true))){
            file.append(Double.toString(tmp));
            file.flush();
            file.newLine();
        }catch (IOException exp){
            pw.println("Error!!!");
            pw.println("File writing handling problem!");
            exp.printStackTrace();
        }
    }

    public void write(String str){
        File fout = new File(filepath);
        try(BufferedWriter file = new BufferedWriter(new FileWriter(fout, true))){
            file.append(str);
            file.flush();
            file.newLine();
        }catch (IOException exp){
            pw.println("Error!!!");
            pw.println("File writing handling problem!");
            exp.printStackTrace();
        }
    }

    public void cleanFile(){
        try(FileWriter fileWriter = new FileWriter(filepath)){
            fileWriter.write("");
        }catch (IOException exp){
            pw.println(exp.getStackTrace());
        }
    }

    public void changePath(String newPath){
        filepath = newPath;
    }

    public void setSpace(String space){
        this.space = space;
    }
}
