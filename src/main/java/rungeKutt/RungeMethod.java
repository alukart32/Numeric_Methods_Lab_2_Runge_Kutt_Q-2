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

    double
            x,
            y,
            //след. шаг
            hnext,
            A,
            B,
            hmin,
            eps;

    boolean getHmin = false;
    // точки с достигнутой точностью
    int precisionPoints = 0;
    // точки с недостигнутой точностью
    int notPrecisionPoints = 0;
    // точки интегрирование где происходили с 2*hmin
    int hminPoints = 0;

    // если было деление шага, то при выборе след. значения
    // шага интегр. h n+1 удвоение пред шага не происходит
    boolean division = false;

    // удвоение шага может быть ограничено при выборе млед шага
    // не более 5
    int mult = 0;

    // ограничение перовоначального шага < 20
    boolean firstH = true;
    int divisionH = 0;

    // true = справо | налево
    // false = слево | напрво
    boolean direction;

    FileWrite fileWrite = new FileWrite();
    int precision = 18;
    String tableSpace = " ";

    public RungeMethod() {}

    public RungeMethod(FileData data) {
        this.data = data;
        data.setData();

        x = data.getC();
        y = data.getY0();
        //след. шаг
        hnext = data.getH();
        A = data.getA();
        B = data.getB();
        hmin = data.getHmin();
        eps = data.getEps();
        direction = data.isDirection();
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

    private double f(double x, double y){ return 6*x*x;}

    private double embedeedFormula(double x, double y, double h){
        return  1;
    }

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

    private  double rungePrecision(double y1, double y2){
        return Math.abs(y2 - y1)/(0.75);
    }

    private boolean step() {
        // дальнейшее интегрирование будет происходить из точки
        // x n+1 = x n + h n с шагом h n+1
        // шаг h n+1 выбирается так:

        // нет ограничения на деление шага больше
        firstH = false;

        // сохраняем тек. шаг h n
        hn = hnext;
        // проверка на конец интервала
        if ((direction ? x + hnext - A : B - (x + hnext)) < hmin) {
            hnext = hn / 2;
            return false;
        } else {
            // находим h n+1
            if (Math.abs(localeps) < eps / 4.0) {
                if (!division) {
                    if (mult < 5) {
                        hnext *= 2;
                        mult++;
                    }
                }
            } else
                // локальная погрешность между
                hnext = hnext;

            // след точка
            x += hn;
            y = Math.abs(yH);

            precisionPoints++;

            fileWrite.write(precision, x, y, Math.abs(hn), localeps);

            division = false;

            return true;
        }
    }

    private void atLastPoint() {

        hnext = direction ? x - A : B - x;
        localeps = rungeEstimate(x, y, hnext);
        y = yH;
        x += direction ? -Math.abs(hnext) : hnext;

        if (!(Math.abs(localeps) > eps)) {
            precisionPoints++;
        } else {
            System.out.println("Последняя точка");
        }
    }

    private void actionLastPoint(){
        y = yH;
        x += direction ? -Math.abs(hnext) : hnext;
        fileWrite.write(precision, x, y, Math.abs(hnext), localeps);
    }
    private void getSolution(){

        if(direction)
            hnext = -hnext;

        fileWrite.cleanFile();
        fileWrite.setSpace(tableSpace);

        String space = "            ";
        fileWrite.write(space + " " + "X\t\t"+space+" Y\t\t"+space+" H\t\t"+space+" Eps");
        fileWrite.write(precision, x, y, hnext, localeps);

        while (true) {
            localeps = rungeEstimate(x, y, hnext);
            // если локальная погрешность метода в точке xn + h такая,
            // то приближённое решение считается неудов. по точности и
            // выбирается новое значение шага h/=2
            if (!(Math.abs(localeps)>eps)){
                // если вышли за границу отрезка, то останов
                if(!step())
                    break;
            }
            else{
                if(getHmin){
                    notPrecisionPoints++;
                    if(!step())
                        break;
                    getHmin = false;
                }else {
                    if (firstH) {
                        if (divisionH < 25) {
                            if (Math.abs(hnext / 2.0) < 2 * hmin) {
                                hnext = direction ? -2 * hmin : 2 * hmin;
                                getHmin = true;
                            } else {
                                hnext /= 2;
                                divisionH++;
                                division = true;
                            }
                        }
                    } else if (Math.abs(hnext / 2.0) < 2 * hmin)
                        hnext = direction ? -2 * hmin : 2 * hmin;
                    else {
                        hnext /= 2;
                        division = true;
                    }
                }
            }
        }

        // необходимо сделать 1 или 2 шага до конца
        if((direction? x-A:B-x) >= 2*hmin){
            hnext = direction ? -(x - hmin - A) : B - hmin - x;
            localeps = rungeEstimate(x, y, hnext);

            if (!(Math.abs(localeps)>eps)) {
                precisionPoints++;
            }

            // y n + 1
            actionLastPoint();
            hnext = direction ? -x - A : B-x;
            localeps = rungeEstimate(x, y, hnext);

            if (!(Math.abs(localeps)>eps)) {
                precisionPoints++;
            }
            // y n + 2
            actionLastPoint();
        }else
            if((direction ? x - A : B - x) <= 1.5*hmin){
                atLastPoint();
            }
            else{
                hnext = (direction ? x - A : B - x)/2.0;
                localeps = rungeEstimate(x, y, hnext);

                if (!(Math.abs(localeps)>eps)) {
                    precisionPoints++;
                }
                actionLastPoint();
                atLastPoint();
            }

        fileWrite.write("\n\nДостигнута точность: " + precisionPoints);
        fileWrite.write("Достигнута точность: " + notPrecisionPoints);
    }
}