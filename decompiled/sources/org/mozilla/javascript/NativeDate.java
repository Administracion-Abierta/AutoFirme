package org.mozilla.javascript;

import com.sun.jna.platform.win32.WinError;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeDate.class */
final class NativeDate extends IdScriptableObject {
    private static final long serialVersionUID = -8307438915861678966L;
    private static final Object DATE_TAG;
    private static final String js_NaN_date_str = "Invalid Date";
    private static final double HalfTimeDomain = 8.64E15d;
    private static final double HoursPerDay = 24.0d;
    private static final double MinutesPerHour = 60.0d;
    private static final double SecondsPerMinute = 60.0d;
    private static final double msPerSecond = 1000.0d;
    private static final double MinutesPerDay = 1440.0d;
    private static final double SecondsPerDay = 86400.0d;
    private static final double SecondsPerHour = 3600.0d;
    private static final double msPerDay = 8.64E7d;
    private static final double msPerHour = 3600000.0d;
    private static final double msPerMinute = 60000.0d;
    private static final int MAXARGS = 7;
    private static final int ConstructorId_now = -3;
    private static final int ConstructorId_parse = -2;
    private static final int ConstructorId_UTC = -1;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toTimeString = 3;
    private static final int Id_toDateString = 4;
    private static final int Id_toLocaleString = 5;
    private static final int Id_toLocaleTimeString = 6;
    private static final int Id_toLocaleDateString = 7;
    private static final int Id_toUTCString = 8;
    private static final int Id_toSource = 9;
    private static final int Id_valueOf = 10;
    private static final int Id_getTime = 11;
    private static final int Id_getYear = 12;
    private static final int Id_getFullYear = 13;
    private static final int Id_getUTCFullYear = 14;
    private static final int Id_getMonth = 15;
    private static final int Id_getUTCMonth = 16;
    private static final int Id_getDate = 17;
    private static final int Id_getUTCDate = 18;
    private static final int Id_getDay = 19;
    private static final int Id_getUTCDay = 20;
    private static final int Id_getHours = 21;
    private static final int Id_getUTCHours = 22;
    private static final int Id_getMinutes = 23;
    private static final int Id_getUTCMinutes = 24;
    private static final int Id_getSeconds = 25;
    private static final int Id_getUTCSeconds = 26;
    private static final int Id_getMilliseconds = 27;
    private static final int Id_getUTCMilliseconds = 28;
    private static final int Id_getTimezoneOffset = 29;
    private static final int Id_setTime = 30;
    private static final int Id_setMilliseconds = 31;
    private static final int Id_setUTCMilliseconds = 32;
    private static final int Id_setSeconds = 33;
    private static final int Id_setUTCSeconds = 34;
    private static final int Id_setMinutes = 35;
    private static final int Id_setUTCMinutes = 36;
    private static final int Id_setHours = 37;
    private static final int Id_setUTCHours = 38;
    private static final int Id_setDate = 39;
    private static final int Id_setUTCDate = 40;
    private static final int Id_setMonth = 41;
    private static final int Id_setUTCMonth = 42;
    private static final int Id_setFullYear = 43;
    private static final int Id_setUTCFullYear = 44;
    private static final int Id_setYear = 45;
    private static final int Id_toISOString = 46;
    private static final int Id_toJSON = 47;
    private static final int MAX_PROTOTYPE_ID = 47;
    private static final int Id_toGMTString = 8;
    private static final TimeZone thisTimeZone;
    private static final double LocalTZA;
    private static final DateFormat timeZoneFormatter;
    private static final DateFormat localeDateTimeFormatter;
    private static final DateFormat localeDateFormatter;
    private static final DateFormat localeTimeFormatter;
    private double date;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !NativeDate.class.desiredAssertionStatus();
        DATE_TAG = "Date";
        thisTimeZone = TimeZone.getDefault();
        LocalTZA = thisTimeZone.getRawOffset();
        timeZoneFormatter = new SimpleDateFormat("zzz");
        localeDateTimeFormatter = new SimpleDateFormat("MMMM d, yyyy h:mm:ss a z");
        localeDateFormatter = new SimpleDateFormat("MMMM d, yyyy");
        localeTimeFormatter = new SimpleDateFormat("h:mm:ss a z");
    }

    static void init(Scriptable scope, boolean sealed) {
        NativeDate obj = new NativeDate();
        obj.date = Double.NaN;
        obj.exportAsJSClass(47, scope, sealed);
    }

    private NativeDate() {
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "Date";
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Object getDefaultValue(Class<?> typeHint) {
        if (typeHint == null) {
            typeHint = ScriptRuntime.StringClass;
        }
        return super.getDefaultValue(typeHint);
    }

    double getJSTimeValue() {
        return this.date;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        addIdFunctionProperty(ctor, DATE_TAG, -3, "now", 0);
        addIdFunctionProperty(ctor, DATE_TAG, -2, "parse", 1);
        addIdFunctionProperty(ctor, DATE_TAG, -1, "UTC", 7);
        super.fillConstructorProperties(ctor);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        switch (id) {
            case 1:
                arity = 7;
                s = "constructor";
                break;
            case 2:
                arity = 0;
                s = "toString";
                break;
            case 3:
                arity = 0;
                s = "toTimeString";
                break;
            case 4:
                arity = 0;
                s = "toDateString";
                break;
            case 5:
                arity = 0;
                s = "toLocaleString";
                break;
            case 6:
                arity = 0;
                s = "toLocaleTimeString";
                break;
            case 7:
                arity = 0;
                s = "toLocaleDateString";
                break;
            case 8:
                arity = 0;
                s = "toUTCString";
                break;
            case 9:
                arity = 0;
                s = "toSource";
                break;
            case 10:
                arity = 0;
                s = "valueOf";
                break;
            case 11:
                arity = 0;
                s = "getTime";
                break;
            case 12:
                arity = 0;
                s = "getYear";
                break;
            case 13:
                arity = 0;
                s = "getFullYear";
                break;
            case 14:
                arity = 0;
                s = "getUTCFullYear";
                break;
            case 15:
                arity = 0;
                s = "getMonth";
                break;
            case 16:
                arity = 0;
                s = "getUTCMonth";
                break;
            case 17:
                arity = 0;
                s = "getDate";
                break;
            case 18:
                arity = 0;
                s = "getUTCDate";
                break;
            case 19:
                arity = 0;
                s = "getDay";
                break;
            case 20:
                arity = 0;
                s = "getUTCDay";
                break;
            case 21:
                arity = 0;
                s = "getHours";
                break;
            case 22:
                arity = 0;
                s = "getUTCHours";
                break;
            case 23:
                arity = 0;
                s = "getMinutes";
                break;
            case 24:
                arity = 0;
                s = "getUTCMinutes";
                break;
            case 25:
                arity = 0;
                s = "getSeconds";
                break;
            case 26:
                arity = 0;
                s = "getUTCSeconds";
                break;
            case 27:
                arity = 0;
                s = "getMilliseconds";
                break;
            case 28:
                arity = 0;
                s = "getUTCMilliseconds";
                break;
            case 29:
                arity = 0;
                s = "getTimezoneOffset";
                break;
            case 30:
                arity = 1;
                s = "setTime";
                break;
            case 31:
                arity = 1;
                s = "setMilliseconds";
                break;
            case 32:
                arity = 1;
                s = "setUTCMilliseconds";
                break;
            case 33:
                arity = 2;
                s = "setSeconds";
                break;
            case 34:
                arity = 2;
                s = "setUTCSeconds";
                break;
            case 35:
                arity = 3;
                s = "setMinutes";
                break;
            case 36:
                arity = 3;
                s = "setUTCMinutes";
                break;
            case 37:
                arity = 4;
                s = "setHours";
                break;
            case 38:
                arity = 4;
                s = "setUTCHours";
                break;
            case 39:
                arity = 1;
                s = "setDate";
                break;
            case 40:
                arity = 1;
                s = "setUTCDate";
                break;
            case 41:
                arity = 2;
                s = "setMonth";
                break;
            case 42:
                arity = 2;
                s = "setUTCMonth";
                break;
            case 43:
                arity = 3;
                s = "setFullYear";
                break;
            case 44:
                arity = 3;
                s = "setUTCFullYear";
                break;
            case 45:
                arity = 1;
                s = "setYear";
                break;
            case 46:
                arity = 0;
                s = "toISOString";
                break;
            case 47:
                arity = 1;
                s = "toJSON";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(DATE_TAG, id, s, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        double t;
        double t2;
        if (!f.hasTag(DATE_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case -3:
                return ScriptRuntime.wrapNumber(now());
            case -2:
                String dataStr = ScriptRuntime.toString(args, 0);
                return ScriptRuntime.wrapNumber(date_parseString(dataStr));
            case -1:
                return ScriptRuntime.wrapNumber(jsStaticFunction_UTC(args));
            case 1:
                if (thisObj != null) {
                    return date_format(now(), 2);
                }
                return jsConstructor(args);
            case 47:
                Scriptable o = ScriptRuntime.toObject(cx, scope, thisObj);
                Object tv = ScriptRuntime.toPrimitive(o, ScriptRuntime.NumberClass);
                if (tv instanceof Number) {
                    double d = ((Number) tv).doubleValue();
                    if (Double.isNaN(d) || Double.isInfinite(d)) {
                        return null;
                    }
                }
                Object toISO = ScriptableObject.getProperty(o, "toISOString");
                if (toISO == NOT_FOUND) {
                    throw ScriptRuntime.typeError2("msg.function.not.found.in", "toISOString", ScriptRuntime.toString(o));
                }
                if (!(toISO instanceof Callable)) {
                    throw ScriptRuntime.typeError3("msg.isnt.function.in", "toISOString", ScriptRuntime.toString(o), ScriptRuntime.toString(toISO));
                }
                Object result = ((Callable) toISO).call(cx, scope, o, ScriptRuntime.emptyArgs);
                if (!ScriptRuntime.isPrimitive(result)) {
                    throw ScriptRuntime.typeError1("msg.toisostring.must.return.primitive", ScriptRuntime.toString(result));
                }
                return result;
            default:
                if (!(thisObj instanceof NativeDate)) {
                    throw incompatibleCallError(f);
                }
                NativeDate realThis = (NativeDate) thisObj;
                double t3 = realThis.date;
                switch (id) {
                    case 2:
                    case 3:
                    case 4:
                        if (!Double.isNaN(t3)) {
                            return date_format(t3, id);
                        }
                        return js_NaN_date_str;
                    case 5:
                    case 6:
                    case 7:
                        if (!Double.isNaN(t3)) {
                            return toLocale_helper(t3, id);
                        }
                        return js_NaN_date_str;
                    case 8:
                        if (!Double.isNaN(t3)) {
                            return js_toUTCString(t3);
                        }
                        return js_NaN_date_str;
                    case 9:
                        return "(new Date(" + ScriptRuntime.toString(t3) + "))";
                    case 10:
                    case 11:
                        return ScriptRuntime.wrapNumber(t3);
                    case 12:
                    case 13:
                    case 14:
                        if (!Double.isNaN(t3)) {
                            if (id != 14) {
                                t3 = LocalTime(t3);
                            }
                            t3 = YearFromTime(t3);
                            if (id == 12) {
                                if (!cx.hasFeature(1)) {
                                    t3 -= 1900.0d;
                                } else if (1900.0d <= t3 && t3 < 2000.0d) {
                                    t3 -= 1900.0d;
                                }
                            }
                        }
                        return ScriptRuntime.wrapNumber(t3);
                    case 15:
                    case 16:
                        if (!Double.isNaN(t3)) {
                            if (id == 15) {
                                t3 = LocalTime(t3);
                            }
                            t3 = MonthFromTime(t3);
                        }
                        return ScriptRuntime.wrapNumber(t3);
                    case 17:
                    case 18:
                        if (!Double.isNaN(t3)) {
                            if (id == 17) {
                                t3 = LocalTime(t3);
                            }
                            t3 = DateFromTime(t3);
                        }
                        return ScriptRuntime.wrapNumber(t3);
                    case 19:
                    case 20:
                        if (!Double.isNaN(t3)) {
                            if (id == 19) {
                                t3 = LocalTime(t3);
                            }
                            t3 = WeekDay(t3);
                        }
                        return ScriptRuntime.wrapNumber(t3);
                    case 21:
                    case 22:
                        if (!Double.isNaN(t3)) {
                            if (id == 21) {
                                t3 = LocalTime(t3);
                            }
                            t3 = HourFromTime(t3);
                        }
                        return ScriptRuntime.wrapNumber(t3);
                    case 23:
                    case 24:
                        if (!Double.isNaN(t3)) {
                            if (id == 23) {
                                t3 = LocalTime(t3);
                            }
                            t3 = MinFromTime(t3);
                        }
                        return ScriptRuntime.wrapNumber(t3);
                    case 25:
                    case 26:
                        if (!Double.isNaN(t3)) {
                            if (id == 25) {
                                t3 = LocalTime(t3);
                            }
                            t3 = SecFromTime(t3);
                        }
                        return ScriptRuntime.wrapNumber(t3);
                    case 27:
                    case 28:
                        if (!Double.isNaN(t3)) {
                            if (id == 27) {
                                t3 = LocalTime(t3);
                            }
                            t3 = msFromTime(t3);
                        }
                        return ScriptRuntime.wrapNumber(t3);
                    case 29:
                        if (!Double.isNaN(t3)) {
                            t3 = (t3 - LocalTime(t3)) / msPerMinute;
                        }
                        return ScriptRuntime.wrapNumber(t3);
                    case 30:
                        double t4 = TimeClip(ScriptRuntime.toNumber(args, 0));
                        realThis.date = t4;
                        return ScriptRuntime.wrapNumber(t4);
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                        double t5 = makeTime(t3, args, id);
                        realThis.date = t5;
                        return ScriptRuntime.wrapNumber(t5);
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                        double t6 = makeDate(t3, args, id);
                        realThis.date = t6;
                        return ScriptRuntime.wrapNumber(t6);
                    case 45:
                        double year = ScriptRuntime.toNumber(args, 0);
                        if (Double.isNaN(year) || Double.isInfinite(year)) {
                            t = Double.NaN;
                        } else {
                            if (Double.isNaN(t3)) {
                                t2 = 0.0d;
                            } else {
                                t2 = LocalTime(t3);
                            }
                            if (year >= 0.0d && year <= 99.0d) {
                                year += 1900.0d;
                            }
                            double day = MakeDay(year, MonthFromTime(t2), DateFromTime(t2));
                            t = TimeClip(internalUTC(MakeDate(day, TimeWithinDay(t2))));
                        }
                        realThis.date = t;
                        return ScriptRuntime.wrapNumber(t);
                    case 46:
                        if (!Double.isNaN(t3)) {
                            return js_toISOString(t3);
                        }
                        String msg = ScriptRuntime.getMessage0("msg.invalid.date");
                        throw ScriptRuntime.rangeError(msg);
                    default:
                        throw new IllegalArgumentException(String.valueOf(id));
                }
        }
    }

    private static double Day(double t) {
        return Math.floor(t / msPerDay);
    }

    private static double TimeWithinDay(double t) {
        double result = t % msPerDay;
        if (result < 0.0d) {
            result += msPerDay;
        }
        return result;
    }

    private static boolean IsLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    private static double DayFromYear(double y) {
        return (((365.0d * (y - 1970.0d)) + Math.floor((y - 1969.0d) / 4.0d)) - Math.floor((y - 1901.0d) / 100.0d)) + Math.floor((y - 1601.0d) / 400.0d);
    }

    private static double TimeFromYear(double y) {
        return DayFromYear(y) * msPerDay;
    }

    private static int YearFromTime(double t) {
        if (Double.isInfinite(t) || Double.isNaN(t)) {
            return 0;
        }
        double y = Math.floor(t / 3.1556952E10d) + 1970.0d;
        double t2 = TimeFromYear(y);
        if (t2 > t) {
            y -= 1.0d;
        } else if (t2 + (msPerDay * DaysInYear(y)) <= t) {
            y += 1.0d;
        }
        return (int) y;
    }

    private static double DayFromMonth(int m, int year) {
        int day;
        int day2 = m * 30;
        if (m >= 7) {
            day = day2 + ((m / 2) - 1);
        } else {
            day = m >= 2 ? day2 + (((m - 1) / 2) - 1) : day2 + m;
        }
        if (m >= 2 && IsLeapYear(year)) {
            day++;
        }
        return day;
    }

    private static double DaysInYear(double year) {
        if (Double.isInfinite(year) || Double.isNaN(year)) {
            return Double.NaN;
        }
        return IsLeapYear((int) year) ? 366.0d : 365.0d;
    }

    private static int DaysInMonth(int year, int month) {
        if (month == 2) {
            return IsLeapYear(year) ? 29 : 28;
        }
        return month >= 8 ? 31 - (month & 1) : 30 + (month & 1);
    }

    private static int MonthFromTime(double t) {
        int mstart;
        int year = YearFromTime(t);
        int d = ((int) (Day(t) - DayFromYear(year))) - 59;
        if (d < 0) {
            return d < -28 ? 0 : 1;
        }
        if (IsLeapYear(year)) {
            if (d == 0) {
                return 1;
            }
            d--;
        }
        int estimate = d / 30;
        switch (estimate) {
            case 0:
                return 2;
            case 1:
                mstart = 31;
                break;
            case 2:
                mstart = 61;
                break;
            case 3:
                mstart = 92;
                break;
            case 4:
                mstart = 122;
                break;
            case 5:
                mstart = 153;
                break;
            case 6:
                mstart = 184;
                break;
            case 7:
                mstart = 214;
                break;
            case 8:
                mstart = 245;
                break;
            case 9:
                mstart = 275;
                break;
            case 10:
                return 11;
            default:
                throw Kit.codeBug();
        }
        return d >= mstart ? estimate + 2 : estimate + 1;
    }

    private static int DateFromTime(double t) {
        int mdays;
        int mstart;
        int year = YearFromTime(t);
        int d = ((int) (Day(t) - DayFromYear(year))) - 59;
        if (d < 0) {
            return d < -28 ? d + 31 + 28 + 1 : d + 28 + 1;
        }
        if (IsLeapYear(year)) {
            if (d == 0) {
                return 29;
            }
            d--;
        }
        switch (d / 30) {
            case 0:
                return d + 1;
            case 1:
                mdays = 31;
                mstart = 31;
                break;
            case 2:
                mdays = 30;
                mstart = 61;
                break;
            case 3:
                mdays = 31;
                mstart = 92;
                break;
            case 4:
                mdays = 30;
                mstart = 122;
                break;
            case 5:
                mdays = 31;
                mstart = 153;
                break;
            case 6:
                mdays = 31;
                mstart = 184;
                break;
            case 7:
                mdays = 30;
                mstart = 214;
                break;
            case 8:
                mdays = 31;
                mstart = 245;
                break;
            case 9:
                mdays = 30;
                mstart = 275;
                break;
            case 10:
                return (d - WinError.ERROR_EAS_DIDNT_FIT) + 1;
            default:
                throw Kit.codeBug();
        }
        int d2 = d - mstart;
        if (d2 < 0) {
            d2 += mdays;
        }
        return d2 + 1;
    }

    private static int WeekDay(double t) {
        double result = (Day(t) + 4.0d) % 7.0d;
        if (result < 0.0d) {
            result += 7.0d;
        }
        return (int) result;
    }

    private static double now() {
        return System.currentTimeMillis();
    }

    private static double DaylightSavingTA(double t) {
        if (t < 0.0d) {
            int year = EquivalentYear(YearFromTime(t));
            double day = MakeDay(year, MonthFromTime(t), DateFromTime(t));
            t = MakeDate(day, TimeWithinDay(t));
        }
        Date date = new Date((long) t);
        if (thisTimeZone.inDaylightTime(date)) {
            return msPerHour;
        }
        return 0.0d;
    }

    private static int EquivalentYear(int year) {
        int day = (((int) DayFromYear(year)) + 4) % 7;
        if (day < 0) {
            day += 7;
        }
        if (IsLeapYear(year)) {
            switch (day) {
                case 0:
                    return 1984;
                case 1:
                    return 1996;
                case 2:
                    return 1980;
                case 3:
                    return 1992;
                case 4:
                    return 1976;
                case 5:
                    return 1988;
                case 6:
                    return 1972;
            }
        }
        switch (day) {
            case 0:
                return 1978;
            case 1:
                return 1973;
            case 2:
                return 1985;
            case 3:
                return 1986;
            case 4:
                return 1981;
            case 5:
                return 1971;
            case 6:
                return 1977;
        }
        throw Kit.codeBug();
    }

    private static double LocalTime(double t) {
        return t + LocalTZA + DaylightSavingTA(t);
    }

    private static double internalUTC(double t) {
        return (t - LocalTZA) - DaylightSavingTA(t - LocalTZA);
    }

    private static int HourFromTime(double t) {
        double result = Math.floor(t / msPerHour) % HoursPerDay;
        if (result < 0.0d) {
            result += HoursPerDay;
        }
        return (int) result;
    }

    private static int MinFromTime(double t) {
        double result = Math.floor(t / msPerMinute) % 60.0d;
        if (result < 0.0d) {
            result += 60.0d;
        }
        return (int) result;
    }

    private static int SecFromTime(double t) {
        double result = Math.floor(t / msPerSecond) % 60.0d;
        if (result < 0.0d) {
            result += 60.0d;
        }
        return (int) result;
    }

    private static int msFromTime(double t) {
        double result = t % msPerSecond;
        if (result < 0.0d) {
            result += msPerSecond;
        }
        return (int) result;
    }

    private static double MakeTime(double hour, double min, double sec, double ms) {
        return (((((hour * 60.0d) + min) * 60.0d) + sec) * msPerSecond) + ms;
    }

    private static double MakeDay(double year, double month, double date) {
        double year2 = year + Math.floor(month / 12.0d);
        double month2 = month % 12.0d;
        if (month2 < 0.0d) {
            month2 += 12.0d;
        }
        double yearday = Math.floor(TimeFromYear(year2) / msPerDay);
        double monthday = DayFromMonth((int) month2, (int) year2);
        return ((yearday + monthday) + date) - 1.0d;
    }

    private static double MakeDate(double day, double time) {
        return (day * msPerDay) + time;
    }

    private static double TimeClip(double d) {
        if (Double.isNaN(d) || d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY || Math.abs(d) > HalfTimeDomain) {
            return Double.NaN;
        }
        if (d > 0.0d) {
            return Math.floor(d + 0.0d);
        }
        return Math.ceil(d + 0.0d);
    }

    private static double date_msecFromDate(double year, double mon, double mday, double hour, double min, double sec, double msec) {
        double day = MakeDay(year, mon, mday);
        double time = MakeTime(hour, min, sec, msec);
        double result = MakeDate(day, time);
        return result;
    }

    private static double date_msecFromArgs(Object[] args) {
        double[] array = new double[7];
        for (int loop = 0; loop < 7; loop++) {
            if (loop < args.length) {
                double d = ScriptRuntime.toNumber(args[loop]);
                if (Double.isNaN(d) || Double.isInfinite(d)) {
                    return Double.NaN;
                }
                array[loop] = ScriptRuntime.toInteger(args[loop]);
            } else if (loop == 2) {
                array[loop] = 1.0d;
            } else {
                array[loop] = 0.0d;
            }
        }
        if (array[0] >= 0.0d && array[0] <= 99.0d) {
            array[0] = array[0] + 1900.0d;
        }
        return date_msecFromDate(array[0], array[1], array[2], array[3], array[4], array[5], array[6]);
    }

    private static double jsStaticFunction_UTC(Object[] args) {
        if (args.length == 0) {
            return Double.NaN;
        }
        return TimeClip(date_msecFromArgs(args));
    }

    private static double parseISOString(String s) {
        int i;
        int i2;
        int i3;
        int i4;
        int state = 0;
        int[] values = new int[9];
        values[0] = 1970;
        values[1] = 1;
        values[2] = 1;
        values[3] = 0;
        values[4] = 0;
        values[5] = 0;
        values[6] = 0;
        values[7] = -1;
        values[8] = -1;
        int yearlen = 4;
        int yearmod = 1;
        int tzmod = 1;
        int i5 = 0;
        int len = s.length();
        if (len != 0) {
            char c = s.charAt(0);
            if (c == '+' || c == '-') {
                i5 = 0 + 1;
                yearlen = 6;
                yearmod = c == '-' ? -1 : 1;
            } else if (c == 'T') {
                i5 = 0 + 1;
                state = 3;
            }
        }
        while (state != -1) {
            int i6 = i5;
            if (state == 0) {
                i = yearlen;
            } else {
                i = state == 6 ? 3 : 2;
            }
            int m = i6 + i;
            if (m > len) {
                state = -1;
            } else {
                int value = 0;
                while (true) {
                    if (i5 < m) {
                        char c2 = s.charAt(i5);
                        if (c2 < '0' || c2 > '9') {
                            state = -1;
                        } else {
                            value = (10 * value) + (c2 - '0');
                            i5++;
                        }
                    } else {
                        values[state] = value;
                        if (i5 == len) {
                            switch (state) {
                                case 3:
                                case 7:
                                    state = -1;
                                    break;
                            }
                        } else {
                            int i7 = i5;
                            i5++;
                            char c3 = s.charAt(i7);
                            if (c3 == 'Z') {
                                values[7] = 0;
                                values[8] = 0;
                                switch (state) {
                                    case 4:
                                    case 5:
                                    case 6:
                                        break;
                                    default:
                                        state = -1;
                                        break;
                                }
                            } else {
                                switch (state) {
                                    case 0:
                                    case 1:
                                        if (c3 == '-') {
                                            i4 = state + 1;
                                        } else {
                                            i4 = c3 == 'T' ? 3 : -1;
                                        }
                                        state = i4;
                                        break;
                                    case 2:
                                        state = c3 == 'T' ? 3 : -1;
                                        break;
                                    case 3:
                                        state = c3 == ':' ? 4 : -1;
                                        break;
                                    case 4:
                                        if (c3 == ':') {
                                            i3 = 5;
                                        } else {
                                            i3 = (c3 == '+' || c3 == '-') ? 7 : -1;
                                        }
                                        state = i3;
                                        break;
                                    case 5:
                                        if (c3 == '.') {
                                            i2 = 6;
                                        } else {
                                            i2 = (c3 == '+' || c3 == '-') ? 7 : -1;
                                        }
                                        state = i2;
                                        break;
                                    case 6:
                                        state = (c3 == '+' || c3 == '-') ? 7 : -1;
                                        break;
                                    case 7:
                                        if (c3 != ':') {
                                            i5--;
                                        }
                                        state = 8;
                                        break;
                                    case 8:
                                        state = -1;
                                        break;
                                }
                                if (state == 7) {
                                    tzmod = c3 == '-' ? -1 : 1;
                                }
                            }
                        }
                    }
                }
            }
            if (state == -1 && i5 == len) {
                int year = values[0];
                int month = values[1];
                int day = values[2];
                int hour = values[3];
                int min = values[4];
                int sec = values[5];
                int msec = values[6];
                int tzhour = values[7];
                int tzmin = values[8];
                if (year > 275943 || month < 1 || month > 12 || day < 1 || day > DaysInMonth(year, month) || hour > 24) {
                    return Double.NaN;
                }
                if ((hour != 24 || (min <= 0 && sec <= 0 && msec <= 0)) && min <= 59 && sec <= 59 && tzhour <= 23 && tzmin <= 59) {
                    double date = date_msecFromDate(year * yearmod, month - 1, day, hour, min, sec, msec);
                    if (tzhour != -1) {
                        date -= (((double) ((tzhour * 60) + tzmin)) * msPerMinute) * ((double) tzmod);
                    }
                    if (date >= -8.64E15d && date <= HalfTimeDomain) {
                        return date;
                    }
                    return Double.NaN;
                }
                return Double.NaN;
            }
        }
        return state == -1 ? Double.NaN : Double.NaN;
    }

    /* JADX WARN: Code duplicated, block: B:53:0x011e  */
    /* JADX WARN: Code duplicated, block: B:55:0x0128  */
    /* JADX WARN: Code duplicated, block: B:56:0x0132  */
    /* JADX WARN: Code duplicated, block: B:59:0x0149  */
    /* JADX WARN: Code duplicated, block: B:62:0x0155  */
    private static double date_parseString(String s) {
        char c;
        int n;
        double d = parseISOString(s);
        if (!Double.isNaN(d)) {
            return d;
        }
        int year = -1;
        int mon = -1;
        int mday = -1;
        int hour = -1;
        int min = -1;
        int sec = -1;
        int i = 0;
        double tzoffset = -1.0d;
        char prevc = 0;
        boolean seenplusminus = false;
        int limit = s.length();
        while (i < limit) {
            char c2 = s.charAt(i);
            i++;
            if (c2 <= ' ' || c2 == ',' || c2 == '-') {
                if (i < limit) {
                    char si = s.charAt(i);
                    if (c2 == '-' && '0' <= si && si <= '9') {
                        prevc = c2;
                    }
                }
            } else if (c2 == '(') {
                int depth = 1;
                while (i < limit) {
                    char c3 = s.charAt(i);
                    i++;
                    if (c3 == '(') {
                        depth++;
                    } else if (c3 == ')') {
                        depth--;
                        if (depth <= 0) {
                            break;
                        }
                    } else {
                        continue;
                    }
                }
            } else if ('0' <= c2 && c2 <= '9') {
                int n2 = c2 - '0';
                while (i < limit) {
                    char cCharAt = s.charAt(i);
                    c2 = cCharAt;
                    if ('0' <= cCharAt && c2 <= '9') {
                        n2 = ((n2 * 10) + c2) - 48;
                        i++;
                    } else {
                        if (prevc != '+' || prevc == '-') {
                            seenplusminus = true;
                            if (n2 < 24) {
                                n = n2 * 60;
                            } else {
                                n = (n2 % 100) + ((n2 / 100) * 60);
                            }
                            if (prevc == '+') {
                                n = -n;
                            }
                            if (tzoffset == 0.0d && tzoffset != -1.0d) {
                                return Double.NaN;
                            }
                            tzoffset = n;
                        } else if (n2 >= 70 || (prevc == '/' && mon >= 0 && mday >= 0 && year < 0)) {
                            if (year >= 0) {
                                return Double.NaN;
                            }
                            if (c2 <= ' ' || c2 == ',' || c2 == '/' || i >= limit) {
                                year = n2 < 100 ? n2 + WinError.RPC_S_INVALID_OBJECT : n2;
                            } else {
                                return Double.NaN;
                            }
                        } else if (c2 == ':') {
                            if (hour < 0) {
                                hour = n2;
                            } else if (min < 0) {
                                min = n2;
                            } else {
                                return Double.NaN;
                            }
                        } else if (c2 == '/') {
                            if (mon < 0) {
                                mon = n2 - 1;
                            } else if (mday < 0) {
                                mday = n2;
                            } else {
                                return Double.NaN;
                            }
                        } else {
                            if (i < limit && c2 != ',' && c2 > ' ' && c2 != '-') {
                                return Double.NaN;
                            }
                            if (!seenplusminus || n2 >= 60) {
                                if (hour >= 0 && min < 0) {
                                    min = n2;
                                } else if (min >= 0 && sec < 0) {
                                    sec = n2;
                                } else if (mday < 0) {
                                    mday = n2;
                                } else {
                                    return Double.NaN;
                                }
                            } else if (tzoffset < 0.0d) {
                                tzoffset -= (double) n2;
                            } else {
                                tzoffset += (double) n2;
                            }
                        }
                        prevc = 0;
                    }
                }
                if (prevc != '+') {
                    seenplusminus = true;
                    if (n2 < 24) {
                        n = n2 * 60;
                    } else {
                        n = (n2 % 100) + ((n2 / 100) * 60);
                    }
                    if (prevc == '+') {
                        n = -n;
                    }
                    if (tzoffset == 0.0d) {
                    }
                    tzoffset = n;
                } else {
                    seenplusminus = true;
                    if (n2 < 24) {
                        n = n2 * 60;
                    } else {
                        n = (n2 % 100) + ((n2 / 100) * 60);
                    }
                    if (prevc == '+') {
                        n = -n;
                    }
                    if (tzoffset == 0.0d) {
                    }
                    tzoffset = n;
                }
                prevc = 0;
            } else if (c2 == '/' || c2 == ':' || c2 == '+' || c2 == '-') {
                prevc = c2;
            } else {
                int st = i - 1;
                while (i < limit && (('A' <= (c = s.charAt(i)) && c <= 'Z') || ('a' <= c && c <= 'z'))) {
                    i++;
                }
                int letterCount = i - st;
                if (letterCount < 2) {
                    return Double.NaN;
                }
                int index = 0;
                int wtbOffset = 0;
                while (true) {
                    int wtbNext = "am;pm;monday;tuesday;wednesday;thursday;friday;saturday;sunday;january;february;march;april;may;june;july;august;september;october;november;december;gmt;ut;utc;est;edt;cst;cdt;mst;mdt;pst;pdt;".indexOf(59, wtbOffset);
                    if (wtbNext < 0) {
                        return Double.NaN;
                    }
                    if (!"am;pm;monday;tuesday;wednesday;thursday;friday;saturday;sunday;january;february;march;april;may;june;july;august;september;october;november;december;gmt;ut;utc;est;edt;cst;cdt;mst;mdt;pst;pdt;".regionMatches(true, wtbOffset, s, st, letterCount)) {
                        wtbOffset = wtbNext + 1;
                        index++;
                    } else {
                        if (index < 2) {
                            if (hour > 12 || hour < 0) {
                                return Double.NaN;
                            }
                            if (index == 0) {
                                if (hour != 12) {
                                    break;
                                }
                                hour = 0;
                                break;
                            }
                            if (hour == 12) {
                                break;
                            }
                            hour += 12;
                            break;
                        }
                        int index2 = index - 2;
                        if (index2 >= 7) {
                            int index3 = index2 - 7;
                            if (index3 < 12) {
                                if (mon < 0) {
                                    mon = index3;
                                    break;
                                }
                                return Double.NaN;
                            }
                            switch (index3 - 12) {
                                case 0:
                                    tzoffset = 0.0d;
                                    break;
                                case 1:
                                    tzoffset = 0.0d;
                                    break;
                                case 2:
                                    tzoffset = 0.0d;
                                    break;
                                case 3:
                                    tzoffset = 300.0d;
                                    break;
                                case 4:
                                    tzoffset = 240.0d;
                                    break;
                                case 5:
                                    tzoffset = 360.0d;
                                    break;
                                case 6:
                                    tzoffset = 300.0d;
                                    break;
                                case 7:
                                    tzoffset = 420.0d;
                                    break;
                                case 8:
                                    tzoffset = 360.0d;
                                    break;
                                case 9:
                                    tzoffset = 480.0d;
                                    break;
                                case 10:
                                    tzoffset = 420.0d;
                                    break;
                                default:
                                    Kit.codeBug();
                                    break;
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (year < 0 || mon < 0 || mday < 0) {
            return Double.NaN;
        }
        if (sec < 0) {
            sec = 0;
        }
        if (min < 0) {
            min = 0;
        }
        if (hour < 0) {
            hour = 0;
        }
        double msec = date_msecFromDate(year, mon, mday, hour, min, sec, 0.0d);
        if (tzoffset == -1.0d) {
            return internalUTC(msec);
        }
        return msec + (tzoffset * msPerMinute);
    }

    private static String date_format(double t, int methodId) {
        StringBuilder result = new StringBuilder(60);
        double local = LocalTime(t);
        if (methodId != 3) {
            appendWeekDayName(result, WeekDay(local));
            result.append(' ');
            appendMonthName(result, MonthFromTime(local));
            result.append(' ');
            append0PaddedUint(result, DateFromTime(local), 2);
            result.append(' ');
            int year = YearFromTime(local);
            if (year < 0) {
                result.append('-');
                year = -year;
            }
            append0PaddedUint(result, year, 4);
            if (methodId != 4) {
                result.append(' ');
            }
        }
        if (methodId != 4) {
            append0PaddedUint(result, HourFromTime(local), 2);
            result.append(':');
            append0PaddedUint(result, MinFromTime(local), 2);
            result.append(':');
            append0PaddedUint(result, SecFromTime(local), 2);
            int minutes = (int) Math.floor((LocalTZA + DaylightSavingTA(t)) / msPerMinute);
            int offset = ((minutes / 60) * 100) + (minutes % 60);
            if (offset > 0) {
                result.append(" GMT+");
            } else {
                result.append(" GMT-");
                offset = -offset;
            }
            append0PaddedUint(result, offset, 4);
            if (t < 0.0d) {
                int equiv = EquivalentYear(YearFromTime(local));
                double day = MakeDay(equiv, MonthFromTime(t), DateFromTime(t));
                t = MakeDate(day, TimeWithinDay(t));
            }
            result.append(" (");
            Date date = new Date((long) t);
            synchronized (timeZoneFormatter) {
                result.append(timeZoneFormatter.format(date));
            }
            result.append(')');
        }
        return result.toString();
    }

    private static Object jsConstructor(Object[] args) {
        double date;
        NativeDate obj = new NativeDate();
        if (args.length == 0) {
            obj.date = now();
            return obj;
        }
        if (args.length == 1) {
            Object arg0 = args[0];
            if (arg0 instanceof NativeDate) {
                obj.date = ((NativeDate) arg0).date;
                return obj;
            }
            if (arg0 instanceof Scriptable) {
                arg0 = ((Scriptable) arg0).getDefaultValue(null);
            }
            if (arg0 instanceof CharSequence) {
                date = date_parseString(arg0.toString());
            } else {
                date = ScriptRuntime.toNumber(arg0);
            }
            obj.date = TimeClip(date);
            return obj;
        }
        double time = date_msecFromArgs(args);
        if (!Double.isNaN(time) && !Double.isInfinite(time)) {
            time = TimeClip(internalUTC(time));
        }
        obj.date = time;
        return obj;
    }

    private static String toLocale_helper(double t, int methodId) {
        DateFormat formatter;
        String str;
        switch (methodId) {
            case 5:
                formatter = localeDateTimeFormatter;
                break;
            case 6:
                formatter = localeTimeFormatter;
                break;
            case 7:
                formatter = localeDateFormatter;
                break;
            default:
                throw new AssertionError();
        }
        synchronized (formatter) {
            str = formatter.format(new Date((long) t));
        }
        return str;
    }

    private static String js_toUTCString(double date) {
        StringBuilder result = new StringBuilder(60);
        appendWeekDayName(result, WeekDay(date));
        result.append(", ");
        append0PaddedUint(result, DateFromTime(date), 2);
        result.append(' ');
        appendMonthName(result, MonthFromTime(date));
        result.append(' ');
        int year = YearFromTime(date);
        if (year < 0) {
            result.append('-');
            year = -year;
        }
        append0PaddedUint(result, year, 4);
        result.append(' ');
        append0PaddedUint(result, HourFromTime(date), 2);
        result.append(':');
        append0PaddedUint(result, MinFromTime(date), 2);
        result.append(':');
        append0PaddedUint(result, SecFromTime(date), 2);
        result.append(" GMT");
        return result.toString();
    }

    private static String js_toISOString(double t) {
        StringBuilder result = new StringBuilder(27);
        int year = YearFromTime(t);
        if (year < 0) {
            result.append('-');
            append0PaddedUint(result, -year, 6);
        } else if (year > 9999) {
            append0PaddedUint(result, year, 6);
        } else {
            append0PaddedUint(result, year, 4);
        }
        result.append('-');
        append0PaddedUint(result, MonthFromTime(t) + 1, 2);
        result.append('-');
        append0PaddedUint(result, DateFromTime(t), 2);
        result.append('T');
        append0PaddedUint(result, HourFromTime(t), 2);
        result.append(':');
        append0PaddedUint(result, MinFromTime(t), 2);
        result.append(':');
        append0PaddedUint(result, SecFromTime(t), 2);
        result.append('.');
        append0PaddedUint(result, msFromTime(t), 3);
        result.append('Z');
        return result.toString();
    }

    private static void append0PaddedUint(StringBuilder sb, int i, int minWidth) {
        if (i < 0) {
            Kit.codeBug();
        }
        int scale = 1;
        int minWidth2 = minWidth - 1;
        if (i >= 10) {
            if (i < 1000000000) {
                while (true) {
                    int newScale = scale * 10;
                    if (i < newScale) {
                        break;
                    }
                    minWidth2--;
                    scale = newScale;
                }
            } else {
                minWidth2 -= 9;
                scale = 1000000000;
            }
        }
        while (minWidth2 > 0) {
            sb.append('0');
            minWidth2--;
        }
        while (scale != 1) {
            sb.append((char) (48 + (i / scale)));
            i %= scale;
            scale /= 10;
        }
        sb.append((char) (48 + i));
    }

    private static void appendMonthName(StringBuilder sb, int index) {
        int index2 = index * 3;
        for (int i = 0; i != 3; i++) {
            sb.append("JanFebMarAprMayJunJulAugSepOctNovDec".charAt(index2 + i));
        }
    }

    private static void appendWeekDayName(StringBuilder sb, int index) {
        int index2 = index * 3;
        for (int i = 0; i != 3; i++) {
            sb.append("SunMonTueWedThuFriSat".charAt(index2 + i));
        }
    }

    /* JADX WARN: Code duplicated, block: B:20:0x006e  */
    /* JADX WARN: Code duplicated, block: B:21:0x0073  */
    /* JADX WARN: Code duplicated, block: B:24:0x007d  */
    /* JADX WARN: Code duplicated, block: B:31:0x009a  */
    /* JADX WARN: Code duplicated, block: B:33:0x00ab  */
    /* JADX WARN: Code duplicated, block: B:35:0x00b3  */
    /* JADX WARN: Code duplicated, block: B:40:0x00ce  */
    /* JADX WARN: Code duplicated, block: B:42:0x00d5 A[ADDED_TO_REGION, ORIG_RETURN, RETURN] */
    private static double makeTime(double date, Object[] args, int methodId) {
        int maxargs;
        boolean hasNaN;
        int length;
        int numNums;
        int i;
        double lorutime;
        double hour;
        double min;
        double sec;
        double msec;
        double d;
        if (args.length == 0) {
            return Double.NaN;
        }
        boolean local = true;
        switch (methodId) {
            case 32:
                local = false;
            case 31:
                maxargs = 1;
                hasNaN = false;
                if (args.length < maxargs) {
                    length = args.length;
                } else {
                    length = maxargs;
                }
                numNums = length;
                if ($assertionsDisabled && numNums > 4) {
                    throw new AssertionError();
                }
                double[] nums = new double[4];
                for (i = 0; i < numNums; i++) {
                    d = ScriptRuntime.toNumber(args[i]);
                    if (!Double.isNaN(d) || Double.isInfinite(d)) {
                        hasNaN = true;
                    } else {
                        nums[i] = ScriptRuntime.toInteger(d);
                    }
                }
                if (!hasNaN || Double.isNaN(date)) {
                    return Double.NaN;
                }
                int i2 = 0;
                if (local) {
                    lorutime = LocalTime(date);
                } else {
                    lorutime = date;
                }
                if (maxargs >= 4 && 0 < numNums) {
                    i2 = 0 + 1;
                    hour = nums[0];
                } else {
                    hour = HourFromTime(lorutime);
                }
                if (maxargs >= 3 && i2 < numNums) {
                    int i3 = i2;
                    i2++;
                    min = nums[i3];
                } else {
                    min = MinFromTime(lorutime);
                }
                if (maxargs >= 2 && i2 < numNums) {
                    int i4 = i2;
                    i2++;
                    sec = nums[i4];
                } else {
                    sec = SecFromTime(lorutime);
                }
                if (maxargs >= 1 && i2 < numNums) {
                    int i5 = i2;
                    int i6 = i2 + 1;
                    msec = nums[i5];
                } else {
                    msec = msFromTime(lorutime);
                }
                double time = MakeTime(hour, min, sec, msec);
                double result = MakeDate(Day(lorutime), time);
                if (local) {
                    result = internalUTC(result);
                }
                return TimeClip(result);
            case 34:
                local = false;
            case 33:
                maxargs = 2;
                hasNaN = false;
                if (args.length < maxargs) {
                    length = args.length;
                } else {
                    length = maxargs;
                }
                numNums = length;
                if ($assertionsDisabled) {
                    break;
                }
                double[] nums2 = new double[4];
                while (i < numNums) {
                    d = ScriptRuntime.toNumber(args[i]);
                    if (!Double.isNaN(d)) {
                        hasNaN = true;
                    } else {
                        hasNaN = true;
                    }
                }
                if (!hasNaN) {
                    return Double.NaN;
                }
                return Double.NaN;
            case 36:
                local = false;
            case 35:
                maxargs = 3;
                hasNaN = false;
                if (args.length < maxargs) {
                    length = args.length;
                } else {
                    length = maxargs;
                }
                numNums = length;
                if ($assertionsDisabled) {
                    break;
                }
                double[] nums3 = new double[4];
                while (i < numNums) {
                    d = ScriptRuntime.toNumber(args[i]);
                    if (!Double.isNaN(d)) {
                        hasNaN = true;
                    } else {
                        hasNaN = true;
                    }
                }
                if (!hasNaN) {
                    return Double.NaN;
                }
                return Double.NaN;
            case 38:
                local = false;
            case 37:
                maxargs = 4;
                hasNaN = false;
                if (args.length < maxargs) {
                    length = args.length;
                } else {
                    length = maxargs;
                }
                numNums = length;
                if ($assertionsDisabled) {
                    break;
                }
                double[] nums4 = new double[4];
                while (i < numNums) {
                    d = ScriptRuntime.toNumber(args[i]);
                    if (!Double.isNaN(d)) {
                        hasNaN = true;
                    } else {
                        hasNaN = true;
                    }
                }
                if (!hasNaN) {
                    return Double.NaN;
                }
                return Double.NaN;
            default:
                throw Kit.codeBug();
        }
    }

    /* JADX WARN: Code duplicated, block: B:18:0x005d  */
    /* JADX WARN: Code duplicated, block: B:19:0x0062  */
    /* JADX WARN: Code duplicated, block: B:22:0x006c  */
    /* JADX WARN: Code duplicated, block: B:31:0x008f  */
    /* JADX WARN: Code duplicated, block: B:33:0x00a0  */
    /* JADX WARN: Code duplicated, block: B:35:0x00a8  */
    /* JADX WARN: Code duplicated, block: B:40:0x00c3 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:42:0x00c7  */
    /* JADX WARN: Code duplicated, block: B:44:0x00d5  */
    /* JADX WARN: Code duplicated, block: B:46:0x00db A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:48:0x00df  */
    /* JADX WARN: Code duplicated, block: B:49:0x00e5  */
    /* JADX WARN: Code duplicated, block: B:51:0x00ea  */
    /* JADX WARN: Code duplicated, block: B:52:0x00f3  */
    /* JADX WARN: Code duplicated, block: B:55:0x00fc  */
    /* JADX WARN: Code duplicated, block: B:58:0x0110  */
    /* JADX WARN: Code duplicated, block: B:61:0x011e  */
    /* JADX WARN: Code duplicated, block: B:64:0x0132  */
    /* JADX WARN: Code duplicated, block: B:67:0x0140  */
    /* JADX WARN: Code duplicated, block: B:70:0x0154  */
    /* JADX WARN: Code duplicated, block: B:73:0x0178  */
    private static double makeDate(double date, Object[] args, int methodId) {
        int maxargs;
        boolean hasNaN;
        int length;
        int numNums;
        int i;
        double lorutime;
        double year;
        double month;
        double day;
        double result;
        double d;
        if (args.length == 0) {
            return Double.NaN;
        }
        boolean local = true;
        switch (methodId) {
            case 40:
                local = false;
            case 39:
                maxargs = 1;
                hasNaN = false;
                if (args.length < maxargs) {
                    length = args.length;
                } else {
                    length = maxargs;
                }
                numNums = length;
                if ($assertionsDisabled && (1 > numNums || numNums > 3)) {
                    throw new AssertionError();
                }
                double[] nums = new double[3];
                for (i = 0; i < numNums; i++) {
                    d = ScriptRuntime.toNumber(args[i]);
                    if (!Double.isNaN(d) || Double.isInfinite(d)) {
                        hasNaN = true;
                    } else {
                        nums[i] = ScriptRuntime.toInteger(d);
                    }
                }
                if (hasNaN) {
                    return Double.NaN;
                }
                int i2 = 0;
                if (Double.isNaN(date)) {
                    if (maxargs < 3) {
                        return Double.NaN;
                    }
                    lorutime = 0.0d;
                } else if (local) {
                    lorutime = LocalTime(date);
                } else {
                    lorutime = date;
                }
                if (maxargs < 3 && 0 < numNums) {
                    i2 = 0 + 1;
                    year = nums[0];
                } else {
                    year = YearFromTime(lorutime);
                }
                if (maxargs < 2 && i2 < numNums) {
                    int i3 = i2;
                    i2++;
                    month = nums[i3];
                } else {
                    month = MonthFromTime(lorutime);
                }
                if (maxargs < 1 && i2 < numNums) {
                    int i4 = i2;
                    int i5 = i2 + 1;
                    day = nums[i4];
                } else {
                    day = DateFromTime(lorutime);
                }
                result = MakeDate(MakeDay(year, month, day), TimeWithinDay(lorutime));
                if (local) {
                    result = internalUTC(result);
                }
                return TimeClip(result);
            case 42:
                local = false;
            case 41:
                maxargs = 2;
                hasNaN = false;
                if (args.length < maxargs) {
                    length = args.length;
                } else {
                    length = maxargs;
                }
                numNums = length;
                if ($assertionsDisabled) {
                    break;
                }
                double[] nums2 = new double[3];
                while (i < numNums) {
                    d = ScriptRuntime.toNumber(args[i]);
                    if (!Double.isNaN(d)) {
                        hasNaN = true;
                    } else {
                        hasNaN = true;
                    }
                }
                if (hasNaN) {
                    return Double.NaN;
                }
                int i6 = 0;
                if (Double.isNaN(date)) {
                    if (maxargs < 3) {
                        return Double.NaN;
                    }
                    lorutime = 0.0d;
                } else if (local) {
                    lorutime = LocalTime(date);
                } else {
                    lorutime = date;
                }
                if (maxargs < 3) {
                    year = YearFromTime(lorutime);
                } else {
                    year = YearFromTime(lorutime);
                }
                if (maxargs < 2) {
                    month = MonthFromTime(lorutime);
                } else {
                    month = MonthFromTime(lorutime);
                }
                if (maxargs < 1) {
                    day = DateFromTime(lorutime);
                } else {
                    day = DateFromTime(lorutime);
                }
                result = MakeDate(MakeDay(year, month, day), TimeWithinDay(lorutime));
                if (local) {
                    result = internalUTC(result);
                }
                return TimeClip(result);
            case 44:
                local = false;
            case 43:
                maxargs = 3;
                hasNaN = false;
                if (args.length < maxargs) {
                    length = args.length;
                } else {
                    length = maxargs;
                }
                numNums = length;
                if ($assertionsDisabled) {
                    break;
                }
                double[] nums3 = new double[3];
                while (i < numNums) {
                    d = ScriptRuntime.toNumber(args[i]);
                    if (!Double.isNaN(d)) {
                        hasNaN = true;
                    } else {
                        hasNaN = true;
                    }
                }
                if (hasNaN) {
                    return Double.NaN;
                }
                int i7 = 0;
                if (Double.isNaN(date)) {
                    if (maxargs < 3) {
                        return Double.NaN;
                    }
                    lorutime = 0.0d;
                } else if (local) {
                    lorutime = LocalTime(date);
                } else {
                    lorutime = date;
                }
                if (maxargs < 3) {
                    year = YearFromTime(lorutime);
                } else {
                    year = YearFromTime(lorutime);
                }
                if (maxargs < 2) {
                    month = MonthFromTime(lorutime);
                } else {
                    month = MonthFromTime(lorutime);
                }
                if (maxargs < 1) {
                    day = DateFromTime(lorutime);
                } else {
                    day = DateFromTime(lorutime);
                }
                result = MakeDate(MakeDay(year, month, day), TimeWithinDay(lorutime));
                if (local) {
                    result = internalUTC(result);
                }
                return TimeClip(result);
            default:
                throw Kit.codeBug();
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 6:
                int c = s.charAt(0);
                if (c == 103) {
                    X = "getDay";
                    id = 19;
                } else if (c == 116) {
                    X = "toJSON";
                    id = 47;
                }
                break;
            case 7:
                switch (s.charAt(3)) {
                    case 'D':
                        int c2 = s.charAt(0);
                        if (c2 == 103) {
                            X = "getDate";
                            id = 17;
                        } else if (c2 == 115) {
                            X = "setDate";
                            id = 39;
                        }
                        break;
                    case 'T':
                        int c3 = s.charAt(0);
                        if (c3 == 103) {
                            X = "getTime";
                            id = 11;
                        } else if (c3 == 115) {
                            X = "setTime";
                            id = 30;
                        }
                        break;
                    case 'Y':
                        int c4 = s.charAt(0);
                        if (c4 == 103) {
                            X = "getYear";
                            id = 12;
                        } else if (c4 == 115) {
                            X = "setYear";
                            id = 45;
                        }
                        break;
                    case 'u':
                        X = "valueOf";
                        id = 10;
                        break;
                }
                break;
            case 8:
                switch (s.charAt(3)) {
                    case 'H':
                        int c5 = s.charAt(0);
                        if (c5 == 103) {
                            X = "getHours";
                            id = 21;
                        } else if (c5 == 115) {
                            X = "setHours";
                            id = 37;
                        }
                        break;
                    case 'M':
                        int c6 = s.charAt(0);
                        if (c6 == 103) {
                            X = "getMonth";
                            id = 15;
                        } else if (c6 == 115) {
                            X = "setMonth";
                            id = 41;
                        }
                        break;
                    case 'o':
                        X = "toSource";
                        id = 9;
                        break;
                    case 't':
                        X = "toString";
                        id = 2;
                        break;
                }
                break;
            case 9:
                X = "getUTCDay";
                id = 20;
                break;
            case 10:
                int c7 = s.charAt(3);
                if (c7 == 77) {
                    int c8 = s.charAt(0);
                    if (c8 == 103) {
                        X = "getMinutes";
                        id = 23;
                    } else if (c8 == 115) {
                        X = "setMinutes";
                        id = 35;
                    }
                } else if (c7 == 83) {
                    int c9 = s.charAt(0);
                    if (c9 == 103) {
                        X = "getSeconds";
                        id = 25;
                    } else if (c9 == 115) {
                        X = "setSeconds";
                        id = 33;
                    }
                } else if (c7 == 85) {
                    int c10 = s.charAt(0);
                    if (c10 == 103) {
                        X = "getUTCDate";
                        id = 18;
                    } else if (c10 == 115) {
                        X = "setUTCDate";
                        id = 40;
                    }
                }
                break;
            case 11:
                switch (s.charAt(3)) {
                    case 'F':
                        int c11 = s.charAt(0);
                        if (c11 == 103) {
                            X = "getFullYear";
                            id = 13;
                        } else if (c11 == 115) {
                            X = "setFullYear";
                            id = 43;
                        }
                        break;
                    case 'M':
                        X = "toGMTString";
                        id = 8;
                        break;
                    case 'S':
                        X = "toISOString";
                        id = 46;
                        break;
                    case 'T':
                        X = "toUTCString";
                        id = 8;
                        break;
                    case 'U':
                        int c12 = s.charAt(0);
                        if (c12 == 103) {
                            int c13 = s.charAt(9);
                            if (c13 == 114) {
                                X = "getUTCHours";
                                id = 22;
                            } else if (c13 == 116) {
                                X = "getUTCMonth";
                                id = 16;
                            }
                        } else if (c12 == 115) {
                            int c14 = s.charAt(9);
                            if (c14 == 114) {
                                X = "setUTCHours";
                                id = 38;
                            } else if (c14 == 116) {
                                X = "setUTCMonth";
                                id = 42;
                            }
                        }
                        break;
                    case 's':
                        X = "constructor";
                        id = 1;
                        break;
                }
                break;
            case 12:
                int c15 = s.charAt(2);
                if (c15 == 68) {
                    X = "toDateString";
                    id = 4;
                } else if (c15 == 84) {
                    X = "toTimeString";
                    id = 3;
                }
                break;
            case 13:
                int c16 = s.charAt(0);
                if (c16 == 103) {
                    int c17 = s.charAt(6);
                    if (c17 == 77) {
                        X = "getUTCMinutes";
                        id = 24;
                    } else if (c17 == 83) {
                        X = "getUTCSeconds";
                        id = 26;
                    }
                } else if (c16 == 115) {
                    int c18 = s.charAt(6);
                    if (c18 == 77) {
                        X = "setUTCMinutes";
                        id = 36;
                    } else if (c18 == 83) {
                        X = "setUTCSeconds";
                        id = 34;
                    }
                }
                break;
            case 14:
                int c19 = s.charAt(0);
                if (c19 == 103) {
                    X = "getUTCFullYear";
                    id = 14;
                } else if (c19 == 115) {
                    X = "setUTCFullYear";
                    id = 44;
                } else if (c19 == 116) {
                    X = "toLocaleString";
                    id = 5;
                }
                break;
            case 15:
                int c20 = s.charAt(0);
                if (c20 == 103) {
                    X = "getMilliseconds";
                    id = 27;
                } else if (c20 == 115) {
                    X = "setMilliseconds";
                    id = 31;
                }
                break;
            case 17:
                X = "getTimezoneOffset";
                id = 29;
                break;
            case 18:
                int c21 = s.charAt(0);
                if (c21 == 103) {
                    X = "getUTCMilliseconds";
                    id = 28;
                } else if (c21 == 115) {
                    X = "setUTCMilliseconds";
                    id = 32;
                } else if (c21 == 116) {
                    int c22 = s.charAt(8);
                    if (c22 == 68) {
                        X = "toLocaleDateString";
                        id = 7;
                    } else if (c22 == 84) {
                        X = "toLocaleTimeString";
                        id = 6;
                    }
                }
                break;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }
}
