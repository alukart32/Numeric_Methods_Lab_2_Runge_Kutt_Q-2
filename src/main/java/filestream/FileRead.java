package filestream;

import filestream.parser.StringParser;

import java.io.*;

public class FileRead {
    // pathname of input files
    final private String filepath = "E:\\Programming\\Numeric_Method_Runge_Kutt_2_Method\\src\\main\\resources\\input.txt";
    private PrintWriter pw = new PrintWriter(System.out, true);

    public double[] readData(){
        double[] data = null;

        File file = new File(filepath);
        try(BufferedReader fin = new BufferedReader(
                new FileReader(file))
        ){
            // temp string to hold all data from file to parse into double
            StringBuffer strData = new StringBuffer("");
            // reading values from file and save them in a string
            String str = fin.readLine();
            while (str != null){
                strData.append(str);
                strData.append(' ');
                str = fin.readLine();
            }
            // string parsing to get these values
            StringParser stringParser = new StringParser();
            data = stringParser.getData(new String(strData));
            return data;
        }catch(IOException e){
            pw.println("File reading handling error!");
            e.printStackTrace();
        }
        return null;
    }
}