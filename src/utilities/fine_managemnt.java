package utilities;

import java.sql.Date;

public class fine_managemnt {
    private  double fine;
    private Date b_date;
    private Date r_date;

    public double calFine(Date B_date, Date R_date){
        long diff = R_date.getTime() - B_date.getTime();
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
        double fine = 0.0;
        if (diffDays > 14) {
            fine = (diffDays - 14) * 10; // Fine per day 10 RS.
        }
        return fine;
    }

}
