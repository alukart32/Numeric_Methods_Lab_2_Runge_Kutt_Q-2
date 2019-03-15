package rungeKutt;

import data.FileData;
import filestream.FileWrite;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RungeMethod {

    FileData data;
    double
            localeps,
            // сохраняем текущее h для x n+1 = x n + h n
            hn = 0;

    public RungeMethod() {}

    public RungeMethod(FileData data) {
        this.data = data;
        data.setData();
    }

    public void solve(){
        getSolution();
    }

    private double getMachineEps(){
        double R = 1.0;

        while((1.0+R) > 1){
            R/=2;
        }
        return 2*R;
    }

    private double f(double x, double y){ return 3*x*x;}

    private double formula(double x, double y, double h){
        double k1 = h*f(x,y);
        double k2 = h*f(x+h,y+k1);
        double result = y + 0.5*(k1 + k2);

        return result;
    }

    double yH;
    private double rungeEstimate(double startX, double startY, double startH){

        //решение полученное по расчёной формуле 121 в startX + startH (x0 + h)
        yH = formula(startX, startY, startH);

        //решение полученное в startX + startH/2 (x0 + h/2)
        double yh1 = formula(startX, startY, startH/2);

        //из точки startX + startH/2 вычислим приб решение к startX + startH
        double yh2 = formula(startX + startH/2, yh1, startH/2);

        // главный часть погрешности метода
        if(rungePrecision(yH, yh2) < getMachineEps()*10)
            return 0.0;
        else
            return rungePrecision(yH, yh2);
    }

    private  double rungePrecision(double yh1, double yh2){
        return Math.abs((yh2 - yh1)/(0.75));
    }

    private void getSolution(){
        double
                x = data.getC(),
                y = data.getY0(),
                //след. шаг
                hnext = data.getH(),
                A = data.getA(),
                B = data.getB(),
                hmin = data.getHmin(),
                eps = data.getEps();

        int precision = 18;
        String tableSpace = " ";
        FileWrite fileWrite = new FileWrite();
        fileWrite.cleanFile();
        fileWrite.setSpace(tableSpace);

        String space = "            ";
        fileWrite.write(space + " " + "X\t\t"+space+" Y\t\t"+space+" H\t\t"+space+" Eps");
        fileWrite.write(precision, x, y, hnext, localeps);
        // если было деление шага, то при выборе след. значения
        // шага интегр. h n+1 удвоение пред шага не происходит
        boolean division = false;

        // удвоение шага может быть ограничено при выборе млед шага
        // не более 5
        int mult = 0;

        // ограничение перовоначального шага < 20
        boolean firstH = true;
        int divisionH = 0;

        while (true) {
            localeps = rungeEstimate(x, y, hnext);
            // если локальная погрешность метода в точке xn + h такая,
            // то приближённое решение считается неудов. по точности и
            // выбирается новое значение шага h/=2
            if (!(Math.abs(localeps)>eps)){
                // дальнейшее интегрирование будет происходить из точки
                // x n+1 = x n + h n с шагом h n+1
                // шаг h n+1 выбирается так:

                // нет ограничения на деление шага больше
                firstH = false;

                // сохраняем тек. шаг h n
                hn = hnext;
                // проверка на конец интервала
                if((B - (x + hnext)) < hmin){
                    hnext = hn/2;
                    break;
                }
                else {
                    // находим h n+1
                    if(Math.abs(localeps) < eps/4.0){
                        if(!division) {
                            if (mult < 5) {
                                hnext *= 2;
                                mult++;
                            }
                        }
                    }
                    else
                        // локальная погрешность между
                        hnext = hnext;

                    // след точка
                    x += hn;
                    y = yH;

                    fileWrite.write(precision, x, y, hn, localeps);

                    division = false;
                }
            }
            else{
                if(firstH){
                    if(divisionH < 25){
                        if(hnext/2 < 2*hmin)
                            hnext = 2*hmin;
                        else{
                            hnext/=2;
                            divisionH++;
                            division = true;
                        }
                    }
                }
                else
                    if(hnext/2 < 2*hmin)
                        hnext = 2*hmin;
                    else{
                        hnext/=2;
                        division = true;
                    }
            }
        }

        // необходимо сделать 1 или 2 шага до конца
        if(B-x >= 2*hmin){
            double xn1 = B-hmin;
            hnext = B - hmin - x;
            localeps = rungeEstimate(x, y, hnext);
            // y n + 1
            y = yH;
            x += hnext;
            fileWrite.write(precision, x, y, hnext, localeps);

            hnext = B-x;
            localeps = rungeEstimate(x, y, hnext);
            // y n + 2
            y = yH;
            x += hnext;
            fileWrite.write(precision, x, y, hnext, localeps);
        }else
            if(B - x <= 1.5*hmin){
                hnext = B-x;
                localeps = rungeEstimate(x, y, hnext);
                x+=hnext;
                y = yH;
                fileWrite.write(precision, x, y, hnext, localeps);
            }
            else{
                hnext = (B - x)/2.0;
                localeps = rungeEstimate(x, y, hnext);

                y = yH;
                x+=hnext;
                fileWrite.write(precision, x, y, hnext, localeps);

                hnext = B - x;
                localeps = rungeEstimate(x, y, hnext);
                y = yH;
                x+=hnext;

                fileWrite.write(precision, x, y, hnext, localeps);
        }
    }
}
