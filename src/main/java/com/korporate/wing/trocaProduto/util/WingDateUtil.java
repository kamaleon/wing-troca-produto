package com.korporate.wing.trocaProduto.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class WingDateUtil
{
    public static DateTimeFormatter formatterZonedDateTimeDDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static DateTimeFormatter formatterZonedDateTimeddMMyyyyHHmmss = DateTimeFormatter.ofPattern("dd/MM/yyyy',' HH:mm:ss");
    public static DateTimeFormatter formatterLocalDateDDMMYYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static DateTimeFormatter formatterLocalDateISO8061 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String toStringddMMyyyyHHmmss(ZonedDateTime zonedDateTime)
    {
        if (zonedDateTime != null)
            return zonedDateTime.format(formatterZonedDateTimeddMMyyyyHHmmss);

        return null;
    }

    /*public static String toStringDDMMYYYY(ZonedDateTime zonedDateTime)
    {
        if (zonedDateTime != null)
            return zonedDateTime.format(formatterZonedDateTimeDDMMYYYY);

        return null;
    }

    public static String toStringDDMMYYYY(LocalDate zonedDateTime)
    {
        if (zonedDateTime != null)
            return zonedDateTime.format(formatterLocalDateDDMMYYYY);

        return null;
    }

    public static LocalDate fromStringDDMMYYYY(String data)
    {
        if (data != null)
            return LocalDate.parse(data, formatterLocalDateDDMMYYYY);

        return null;
    }

    public static LocalDate fromStringISO8061(String dateStr)
    {
        if (StringUtil.check(dateStr))
            return LocalDate.parse(dateStr, formatterLocalDateISO8061);

        return null;
    }


    public static String toStringISO8061(LocalDate localDate)
    {
        if (localDate != null)
            return localDate.format(formatterLocalDateISO8061);

        return null;
    }

    public static String toStringISO8061(ZonedDateTime zonedDateTime)
    {
        if (zonedDateTime != null)
            return zonedDateTime.format(formatterLocalDateISO8061);

        return null;
    }*/
}
