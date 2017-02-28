package it.cnr.si.missioni.util;

import it.cnr.si.missioni.awesome.exception.AwesomeException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author Gianfranco Gasparro
 */
public class DateUtils {

	/**
	 * PATTERN_DATE
	 */
	public static final String PATTERN_DATE = "dd/MM/yyyy";

	/**
	 * PATTERN_DATE_FOR_DOCUMENTALE
	 */
	public static final String PATTERN_DATE_FOR_DOCUMENTALE = "yyyy-MM-dd";

	/**
	 * PATTERN_DATETIME_FOR_DOCUMENTALE
	 */
	public static final String PATTERN_DATETIME_FOR_DOCUMENTALE = "yyyy-MM-dd HH:mm:ss";

	/**
	 * PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE
	 */
	public static final String PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE = "yyyy-MM-dd HH:mm";

	/**
	 * PATTERN_DATETIME_NO_SEC_FOR_DOCUMENTALE
	 */
	public static final String PATTERN_DATETIME_WITH_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
	
	/**
	 * PATTERN_DATETIME
	 */
	public static final String PATTERN_DATETIME = "dd/MM/yyyy HH:mm:ss";

	/**
	 * PATTERN_DATETIME_NO_SEC
	 */
	public static final String PATTERN_DATETIME_NO_SEC = "dd/MM/yyyy HH:mm";

	private static final long ONE_HOUR = 60 * 60 * 1000L;

	public static Timestamp max(Timestamp timestamp, Timestamp timestamp1) {

		if(timestamp == null) {
			return timestamp1;
		}

		if(timestamp1 == null) {
			return timestamp;
		}

		return timestamp.after(timestamp1) ? timestamp : timestamp1;
	}

	public static Date max(Date date, Date date1) {

		if(date == null) {
			return date1;
		}

		if(date1 == null) {
			return date;
		}

		return date.after(date1) ? date : date1;
	}

	public static Timestamp min(Timestamp timestamp, Timestamp timestamp1) {

		if(timestamp == null) {
			return timestamp1;
		}

		if(timestamp1 == null) {
			return timestamp;
		}

		return timestamp.before(timestamp1) ? timestamp : timestamp1;
	}

	public static Date min(Date date, Date date1) {

		if(date == null) {
			return date1;
		}

		if(date1 == null) {
			return date;
		}

		return date.before(date1) ? date : date1;
	}

	public static LocalDate truncate(ZonedDateTime data) {
		return data.truncatedTo(ChronoUnit.DAYS).toLocalDate();
	}

	
	public static Timestamp truncate(Timestamp timestamp) {
		return new Timestamp(truncate(((Date)(timestamp))).getTime());
	}

	public static Date truncate(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(11,0);
		calendar.set(12,0);
		calendar.set(13,0);
		calendar.set(14,0);
		return calendar.getTime();
	}

	public static long daysBetweenDates(Date dateFrom, Date dateTo) {

		Calendar from = Calendar.getInstance();
		from.setTime(dateFrom);

		Calendar earlierDate = Calendar.getInstance();
		earlierDate.set(from.get(Calendar.YEAR),from
			.get(Calendar.MONTH),from.get(Calendar.DAY_OF_MONTH),0,0,0);

		Calendar to = Calendar.getInstance();
		to.setTime(dateTo);

		Calendar laterDate = Calendar.getInstance();
		laterDate.set(to.get(Calendar.YEAR),to.get(Calendar.MONTH),to
			.get(Calendar.DAY_OF_MONTH),0,0,0);

		long duration =
			laterDate.getTime().getTime() - earlierDate.getTime().getTime();

		//return (duration + ONE_HOUR) / (24 * ONE_HOUR);
		return (duration ) / (24 * ONE_HOUR);
	}

	/**
	 * si presume che la data_from sia superiore alla data_to altrimenti viene
	 * restituito un valore negativo
	 */
	public static int monthsBetweenDates(Date dateFrom, Date dateTo) {

		Calendar from = Calendar.getInstance();
		from.setTime(dateFrom);

		Calendar to = Calendar.getInstance();
		to.setTime(dateTo);
		
		int yearFrom = from.get(Calendar.YEAR);
		int yearTo = to.get(Calendar.YEAR);

		int monthFrom = from.get(Calendar.MONTH);
		int monthTo = to.get(Calendar.MONTH);
		
		int diffYear = yearTo - yearFrom;
		int diffMonth = monthTo - monthFrom;
		
		return (diffYear * 12) + diffMonth;
	}
	

	public static Date dataContabile(Date date, int year) {

		Calendar dataInizio = Calendar.getInstance();
		dataInizio.set(year,Calendar.JANUARY,1);

		Calendar dataFine = Calendar.getInstance();
		dataFine.set(year,Calendar.DECEMBER,31);

		if(date.before(dataInizio.getTime())) {
			return dataInizio.getTime();
		}
		else if(date.after(dataFine.getTime())) {
			return dataFine.getTime();
		}

		return date;
	}

	public static Timestamp dataContabile(Timestamp timestamp, int year) {

		Calendar dataInizio = Calendar.getInstance();
		dataInizio.set(year,Calendar.JANUARY,1);

		Calendar dataFine = Calendar.getInstance();
		dataFine.set(year,Calendar.DECEMBER,31);

		if(timestamp.before(dataInizio.getTime())) {
			return new Timestamp(dataInizio.getTimeInMillis());
		}
		else if(timestamp.after(dataFine.getTime())) {
			return new Timestamp(dataFine.getTimeInMillis());
		}

		return timestamp;
	}

	public static Timestamp getCurrentTime() {
		return new Timestamp(Calendar.getInstance().getTime().getTime());
	}

	public static Timestamp getCurrentTime(String pattern)
					throws AwesomeException {

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String formattedDate = sdf.format(new Date());
		Date date = null;
		try {
			date = sdf.parse(formattedDate);
		}
		catch(ParseException ex) 
		{
			throw new AwesomeException(CodiciErrore.ERRGEN, "Il pattern " + pattern
				+ " non e' corretto");
		}

		Timestamp timestamp = new Timestamp(date.getTime());
		return timestamp;
	}

	public static Date getCurrentDate(String pattern) throws AwesomeException {

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String formattedDate = sdf.format(new Date());
		Date date = null;
		try {
			date = sdf.parse(formattedDate);
		}
		catch(ParseException ex) 
		{
			throw new AwesomeException(CodiciErrore.ERRGEN, "Il pattern " + pattern
				+ " non e' corretto");
		}
		return date;
	}

	/**
	 * @param
	 * @return
	 * @throws {@link AwesomeException}
	 */
	public static Date parseDate(String aDate) throws AwesomeException {
		return parseDate(aDate,PATTERN_DATE);
	}

	/**
	 * @param
	 * @return
	 * @throws {@link AwesomeException}
	 */
	public static Date parseDate(String aDate, String aPattern)
					throws AwesomeException {

		SimpleDateFormat sdf = new SimpleDateFormat(aPattern);

		try {
			return sdf.parse(aDate);
		}
		catch(ParseException e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "La data " + aDate + " non è nel formato "
				+ aPattern);
		}
	}

	/**
	 * @param
	 * @return
	 * @throws {@link AwesomeException}
	 */
	public static LocalDate parseLocalDate(String aDate) throws AwesomeException {
		return parseLocalDate(aDate,PATTERN_DATE);
	}

	/**
	 * @param
	 * @return
	 * @throws {@link AwesomeException}
	 */
	public static LocalDate parseLocalDate(String aDate, String aPattern)
					throws AwesomeException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(aPattern);

		return LocalDate.parse(aDate, formatter);
	}

	/**
	 * @param
	 * @return
	 * @throws {@link AwesomeException}
	 */
	public static ZonedDateTime parseZonedDateTime(String aDate, String aPattern)
					throws AwesomeException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(aPattern);

		return ZonedDateTime.parse(aDate, formatter);
	}

	/**
	 * @param
	 * @return
	 * @throws {@link AwesomeException}
	 */
	public static Date parseDateWithDefaultPattern(String aDate)
					throws AwesomeException {

		SimpleDateFormat sdf = new SimpleDateFormat();

		try {
			return sdf.parse(aDate);
		}
		catch(ParseException e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "La data " + aDate + " non è nel formato datetime di default ");
		}
	}

	public static Date startOfDate(Date date, String pattern)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(pattern.equals(PATTERN_DATE)) 
		{
			calendar.set(Calendar.HOUR_OF_DAY,0);
			calendar.set(Calendar.MINUTE,0);
			calendar.set(Calendar.SECOND,0);
		}
		else if ( pattern.equals(PATTERN_DATETIME_NO_SEC) )
		{
			calendar.set(Calendar.SECOND,0);
		}
		calendar.set(Calendar.MILLISECOND,0);
		return calendar.getTime();
	}

	public static Date endOfDate(Date date, String pattern) 
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(pattern.equals(PATTERN_DATE)) 
		{
			calendar.set(Calendar.HOUR_OF_DAY,23);
			calendar.set(Calendar.MINUTE,59);
			calendar.set(Calendar.SECOND,59);
		}
		else if ( pattern.equals(PATTERN_DATETIME_NO_SEC) )
		{
			calendar.set(Calendar.SECOND,59);
		}
		calendar.set(Calendar.MILLISECOND,999);
		return calendar.getTime();
	}

	/**
	 * Metodo che torna la data di sistema in base al pattern passato
	 * 
	 * @param <i>aPattern</i> pattern da utilizzare per il formato della data
	 * @return la data di sistema in formato stringa con pattern definito da
	 *         <i>aPattern</i>
	 */
	public static String getSystemData(String aPattern) {
		return getDateAsString(new Date(System.currentTimeMillis()),aPattern);
	}

	/**
	 * @param aDate
	 *            - Una data (ovvero la rappresentazione in String di una data)
	 * @return Il pattern utilizzato dalla data specificata
	 * @throws {@link AwesomeException} se la data specificata non utilizza
	 *         nessuno dei pattern prestabiliti ovvero:<br>
	 *         <div>
	 *         <ul>
	 *         <li>dd/MM/yyyy</li>
	 *         <li>dd/MM/yyyy HH:mm</li>
	 *         <li>dd/MM/yyyy HH:mm:ss</li> </div>
	 */
	public static String getPattern(String aDate) throws AwesomeException {

		switch (aDate.split(":").length) {
			case 3:
				return PATTERN_DATETIME;
			case 2:
				return PATTERN_DATETIME_NO_SEC;
			case 1:
				return PATTERN_DATE;
			default:
				throw new AwesomeException(CodiciErrore.ERRGEN, "Il formato della data " + aDate
					+ " non è presente tra quelli previsti ");
		}
	}

	/**
	 * Metodo che torna la data in formato stringa in base al pattern passato. I
	 * pattern possibili sono:<br>
	 * - <i>PATTERN_DATE</i> = "dd/MM/yyyy"<br>
	 * - <i>PATTERN_DATETIME</i> = "dd/MM/yyyy HH:mm:ss"<br>
	 * - <i>PATTERN_DATETIME_NO_SEC</i> = "dd/MM/yyyy HH:mm"<br>
	 * 
	 * @param data
	 *            data da formattare secondo il pattern passato
	 * @param pattern
	 *            pattern da utilizzare per il formato della data
	 * @return la data in formato stringa con pattern definito da <i>pattern</i>
	 */
	public static String getDateAsString(Date data, String pattern) {

		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ITALY);
		return sdf.format(data);
	}

	public static String getDateAsString(ZonedDateTime data, String pattern) {
		return data.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	public static GregorianCalendar getDate(ZonedDateTime data){
		return GregorianCalendar.from(data);
	}
	
	public static GregorianCalendar getDate(LocalDate data){
		return GregorianCalendar.from(data.atStartOfDay(ZoneId.systemDefault()));
	}
	
	public static String getDateAsString(LocalDate data, String pattern) {
		return data.format(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * Metodo che torna la data in formato stringa dd/MM/yyyy.
	 * 
	 * @param data
	 *            data da formattare
	 * @return la data in formato stringa dd/MM/yyyy
	 */
	public static String getDefaultDateAsString(Date data) {
		return getDateAsString(data,PATTERN_DATE);
	}

	public static String getDefaultDateAsString(LocalDate data) {
		return getDateAsString(data,PATTERN_DATE);
	}

	public static String getDefaultDateAsString(ZonedDateTime data) {
		return getDateAsString(data,PATTERN_DATE);
	}

	/**
	 * Metodo che calcola la data dell'ultimo giorno del mese, a partire da una
	 * data.
	 * 
	 * @param data
	 *            di cui si vuole calcolare l'ultimo giorno.
	 * @return la data dell'ultimo giorno del mese.
	 */
	public static Date getLastDateOfMonth(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,calendar
			.getActualMaximum(Calendar.DAY_OF_MONTH));
		setHHmmssmmm(calendar,23,59,59,999);
		return new Date(calendar.getTimeInMillis());
	}

	public static Date getLastDateOfMonth(String mese, String anno) {

		return getLastDateOfMonth(createCalendarForFirstDayOfNow(mese,anno).getTime());
	}

	public static Date getFirstDateOfMonth(String mese, String anno) {

		return getFirstDateOfMonth(createCalendarForFirstDayOfNow(mese,anno).getTime());
	}
	
	private static Calendar createCalendarForFirstDayOfNow(String mese,String anno)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,Integer.decode(anno).intValue());
		calendar.set(Calendar.MONTH,Integer.decode(mese).intValue() - 1);
		calendar.set(Calendar.DAY_OF_MONTH,1);
		return calendar;
	}

	public static Date getFirstDateOfMonth(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,calendar
			.getActualMinimum(Calendar.DAY_OF_MONTH));
		setHHmmssmmm(calendar,0,1,0,0);
		return calendar.getTime();
	}

	/**
	 * Metodo che imposta l'ora di una data,
	 * 
	 * @param calendar
	 *            data che si deve impostare.
	 * @param ore
	 *            .
	 * @param minuti
	 *            .
	 * @param secondi
	 *            .
	 * @param millisecondi
	 *            .
	 * @return la data con ora impostata in base ai parametri passati.
	 */
	private static void setHHmmssmmm(Calendar calendar, int ore, int minuti,
					int secondi, int millisecondi) {

		calendar.set(Calendar.HOUR_OF_DAY,ore);
		calendar.set(Calendar.MINUTE,minuti);
		calendar.set(Calendar.SECOND,secondi);
		calendar.set(Calendar.MILLISECOND,millisecondi);
	}

	/**
	 * Metodo che aggiunge un numero di mesi pari a <i>mesiDaAggiungere</i> alla
	 * data <i>date</i>. Per il calcolo della data tiene conto dell'ultimo
	 * giorno del mese e degli anni bisestili.
	 * 
	 * @param date
	 *            data che deve essere incrementata in numero di mesi.
	 * @param mesiDaAggiungere
	 *            numero di mesi da aggiungere alla data.
	 * @return la data spostata di un numero di mesi pari a
	 *         <i>mesiDaAggiungere</i>.
	 */
	public static Date addMonthsToDate(Date date, int mesiDaAggiungere) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int anno = calendar.get(Calendar.YEAR);
		int mese = calendar.get(Calendar.MONTH);
		int giorno = calendar.get(Calendar.DAY_OF_MONTH);
		mese += mesiDaAggiungere;

		// se inserito un numero di mesi>11 scala 12 mesi dal mese
		// e incrementa l'anno
		while(mese > 11) {
			mese -= 12;
			anno++;
		}
		calendar.set(Calendar.MONTH,mese);
		calendar.set(Calendar.YEAR,anno);

		// se aprile, giugno, settembre o novembre impostacome ultimo giorno il
		// 30
		if(mese == 3 || mese == 5 || mese == 8 || mese == 10) {
			if(giorno == 31) {
				calendar.set(Calendar.DAY_OF_MONTH,30);
			}
		}
		// se febbraio impostacome ultimo giorno il 29 o 28 a seconda che l'anno
		// sia o no bisestile
		if(mese == 1) {
			if(giorno == 31 || giorno == 30 || giorno == 29) {
				if(isBisestile(anno)) {
					// calendar.set(Calendar.MONTH, mese);
					calendar.set(Calendar.DAY_OF_MONTH,29);
				}
				else {
					// calendar.set(Calendar.MONTH, mese);
					calendar.set(Calendar.DAY_OF_MONTH,28);
				}
			}
		}

		return new Date(calendar.getTimeInMillis());
	}

	/**
	 * Metodo che aggiunge un numero di giorni pari a <i>giorniDaAggiungere</i>
	 * alla data <i>date</i>.
	 * 
	 * @param date
	 *            data che deve essere incrementata in numero di giorni.
	 * @param giorniDaAggiungere
	 *            numero di giorni da aggiungere alla data.
	 * @return la data spostata di un numero di giorni pari a
	 *         <i>giorniDaAggiungere</i>.
	 */
	public static Date addDaysToDate(Date date, int giorniDaAggiungere) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH)
			+ giorniDaAggiungere);

		return new Date(calendar.getTimeInMillis());
	}

	/**
	 * Metodo che verifica se un anno è bisestile.
	 * 
	 * @param anno
	 *            anno da controllare
	 * @return true o false a seconda se un anno è bisestile o no
	 */
	public static boolean isBisestile(int anno) {
		return (anno % 400 == 0) || (((anno % 4) == 0) && ((anno % 100) != 0));
	}

	/**
	 * Metodo che verifica se la <i>data1</i> è null, in tal caso ritorna la
	 * <i>data2</i> altrimenti ritorna <i>data1</i>.
	 * 
	 * @param data1
	 *            data da controllare
	 * @param data2
	 *            data da ritornare se data1 è null
	 * @return <i>data1</i> se diversa da null altrimenti <i>data2</i>
	 */
	public static Date nvl(Date data1, Date data2) {

		Calendar calendar = Calendar.getInstance();

		if(data1 != null)
			calendar.setTime(data1);
		else
			calendar.setTime(data2);

		return new Date(calendar.getTimeInMillis());
	}

	/**
	 * Metodo che ritorna l'anno della <i>dataIn</i> stringa in ingresso.
	 * 
	 * @param dataIn
	 *            data da controllare (nel formato stringa)
	 * @return <i>anno</i> dellea data
	 * @throws AwesomeException
	 *             solleva eccezione se la stringa rappresentante la data non è
	 *             nel forato giusto
	 */
	public static Short calcolaAnnoDaData(String dataIn) throws AwesomeException {

		if(dataIn == null || dataIn.trim().equals("")) {
			return null;
		}

		// TODO: calcolaAnnoDaData
		// non capisco che senso ha.
		// Se sono sicuro che il pattern sia quello mi basterebbe prendere
		// gli ultimi quattro caratteri della stringa.

		return new Short((short)calcolaAnnoDaData(parseDate(dataIn,DateUtils.PATTERN_DATE)));
	}

	/**
	 * Metodo che ritorna l'anno dalla <i>dataIn</i> passata in ingresso.
	 * 
	 * @param dataIn
	 *            data da controllare
	 * @return <i>anno</i> della data
	 */
	public static int calcolaAnnoDaData(Date dataIn) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataIn);
		return calendar.get(Calendar.YEAR);
	}
	
	public static int recuperoGiornoDaData(Date dataIn) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataIn);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public static int recuperoMeseDaData(Date dataIn) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataIn);
		return calendar.get(Calendar.MONTH);
	}
	
	/**

     * Metodo che ritorna in formato stringa <i>meseN</i> .

     *

     * @param meseN

     *            mese da convertire nel formato stringa.

     * @return <i>meseS</i> del mese

     * @throws AwesomeException

     *             solleva eccezione se il valore del mese non è

     *             tra quelli possibili.

     */

    public static String monthFromIntToString(Integer meseN ) throws AwesomeException 
    {
    	String meseS = null;   
    	switch(meseN)
    	{
    	case 1: 
    		meseS = "Gennaio";
    		break;
    	case 2:
    		meseS = "Febbraio";
    		break;
    	case 3:
    		meseS = "Marzo";
    		break;
    	case 4:
    		meseS = "Aprile";
    		break;
    	case 5:
    		meseS = "Maggio";
    		break;
    	case 6:
    		meseS = "Giugno";
    		break;
    	case 7:
    		meseS = "Luglio";
    		break;
    	case 8:
    		meseS = "Agosto";
    		break;
    	case 9:
    		meseS = "Settembre";
    		break;
    	case 10:
    		meseS = "Ottobre";
    		break;
    	case 11:
    		meseS = "Novembre";
    		break;
    	case 12:
    		meseS = "Dicembre";
    		break;
    	default:
    		throw new AwesomeException(CodiciErrore.ERRGEN, "Il mese " + meseN+ " è inesistente.");
    	}          
    	return meseS;               
    }
    
    /**
     * Metodo che calcola l&apos;ultimo secondo del giorno indicato.
     * 
     * @param data
     *            di cui si vuole calcolare l&apos;ultimo giorno.
     * @return la data impostata all&apos;ultimo secondo del giorno.
     */
    public static Date getLastTimeOfDate(Date date)
    {
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTime(date);
	     setHHmmssmmm(calendar,23,59,59,999);
	     return new Date(calendar.getTimeInMillis());
    }
    
    public static Date convertToUtilDate(java.sql.Date aValue)
    {
    	return new Date(((java.sql.Date)aValue).getTime());
    }
    
    public static Date convertToUtilDate(Timestamp aValue)
    {
    	return new Date(((Timestamp)aValue).getTime());
    }
    
    public static int getCurrentYear(){
    	return calcolaAnnoDaData(getCurrentTime());
    }
}