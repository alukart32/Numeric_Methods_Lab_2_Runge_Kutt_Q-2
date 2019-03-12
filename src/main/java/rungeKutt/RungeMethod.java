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
            h, localeps;


    public RungeMethod() {}

    public RungeMethod(FileData data) {
        this.data = data;
        data.setData();
    }

    public void solve(){
        getSolution();
    }

    private double f(double x){ return 3*x*x;}

    private double formula(double x, double y, double h){
        double k1 = h*f(x);
        double k2 = h*f(x+h);
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
        return rungePrecision(yH, yh2);
    }

    private  double rungePrecision(double yh1, double yh2){
        return Math.abs((yh2 - yh1)/(0.75));
    }

    private void getSolution(){
        double
                x = data.getC(),
                y = data.getY0(),
                h = data.getH(),
                A = data.getA(),
                B = data.getB(),
                hmin = data.getHmin(),
                eps = data.getEps();

        String tableSpace = " ";
        FileWrite fileWrite = new FileWrite();
        fileWrite.cleanFile();
        fileWrite.setSpace(tableSpace);

        String space = "  ";
        fileWrite.write(space + " " + "X\t\t"+space+" Y\t\t"+space+" H\t\t"+space+" Eps");
        fileWrite.write(5, x, y, h, localeps);
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
            localeps = rungeEstimate(x, y, h);
            // если локальная погрешность метода в точке xn + h такая,
            // то приближённое решение считается неудов. по точности и
            // выбирается новое значение шага h/=2
            if (!(Math.abs(localeps)>eps)){
                // дальнейшее интегрирование будет происходить из точки
                // x n+1 = x n + h n с шагом h n+1
                // шаг h n+1 выбирается так:

                // нет ограничения на деление шага больше
                firstH = false;

                if(Math.abs(localeps) < eps/4.0){
                    if(!division) {
                        if (mult < 5) {
                            h *= 2;
                            mult++;
                        }
                    }
                }
                else
                    // локальная погрешность между
                    h = h;

                // проверка на конец интервала

                if((B - (x + h)) < hmin){
                    break;
                }
                else {
                    // след точка
                    x += h;
                    y = yH;

                    fileWrite.write(5, x, y, h, localeps);

                    division = false;
                    mult = 0;
                }
            }
            else{
                if(firstH){
                    if(divisionH < 20){
                        if(h/2 < hmin)
                            h = hmin;
                        else{
                            h/=2;
                            divisionH++;
                            division = true;
                        }
                    }
                }
                else
                    if(h/2 < hmin)
                        h = hmin;
                    else{
                        h/=2;
                        division = true;
                    }
            }
        }

        // необходимо сделать 1/2 шага до конца
        if(B-x >= 2*hmin){
            h = B - hmin - x;
            x+=h;
            rungeEstimate(x, y, h);
            y = yH;
            fileWrite.write(5, x, y, h, localeps);

            x = B;
            rungeEstimate(x, y, h);

            y = yH;
            fileWrite.write(5, x, y, h, localeps);
        }else
            if(B - x <= 1.5*hmin){
                x = B;
                rungeEstimate(x, y, h);
                y = yH;

                fileWrite.write(5, x, y, h, localeps);
            }
            else{
                x = x + (B - hmin)/2.0;
                rungeEstimate(x, y, h);

                y = yH;

                fileWrite.write(5, x, y, h, localeps);

                x = B;
                rungeEstimate(x, y, h);
                y = yH;

                fileWrite.write(5, x, y, h, localeps);
        }
    }
}
