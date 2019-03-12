package filestream.parser;

public class StringParser {
    private int getAmountOfElementOfStr(String str) {
        int count = 0;

        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == ' ')
                count++;
            i++;
        }
        return count;
    }

    public double[] getData(String str)throws NumberFormatException{
        int amount = getAmountOfElementOfStr(str);

        // A B C Yc hmin eps
        if(amount != (6)) {
            throw new NumberFormatException();
        }

        double[] arr = new double[amount];

        StringBuffer tmp = new StringBuffer("");

        int i = 0;
        int j = 0;
        while (i < str.length() && j < amount){
            if(str.charAt(i) != ' ')
                tmp.append(str.charAt(i));
            else {
                arr[j] = Double.parseDouble(tmp.toString());
                j++;
                tmp.delete(0, tmp.length());
            }
            i++;
        }

        if(arr[3] == arr[0]){}
        else
            if(arr[3] == arr[1]){}
            else throw new NumberFormatException();

        return arr;
    }
}
