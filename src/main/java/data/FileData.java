package data;

import filestream.FileRead;
import lombok.Data;

@Data
public class FileData {
    private double
                    A, B,
                    y0, C,
                    hmin, eps,
                    h;

    // false - слево |  направо
    // true - справо | налево
    boolean direction = false;

    public void setData(){
        FileRead fileRead = new FileRead();
        double[] arr = fileRead.readData();

        A = arr[0];
        B = arr[1];
        y0 = arr[2];
        C = arr[3];
        hmin = arr[4];
        eps = arr[5];

        if(C == A)
            direction = false;
        else
            if(C == B)
                direction = true;
            else
                throw new RuntimeException();

        h = Math.abs((B-A)/10.0);
    }
}
