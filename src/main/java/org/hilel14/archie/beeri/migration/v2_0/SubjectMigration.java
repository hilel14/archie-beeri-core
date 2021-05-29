package org.hilel14.archie.beeri.migration.v2_0;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hilel14
 */
public class SubjectMigration {

    static final Logger LOGGER = LoggerFactory.getLogger(SubjectMigration.class);
    Set<String> isadFonds = new HashSet<>();
    Set<String> isadSeries = new HashSet<>();
    Set<String> isadFile = new HashSet<>();
    Set<String> dcSubject = new HashSet<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Mandatory arguments: input-file");
            System.out.println("");
            System.exit(1);
        }
        Path inPath = Paths.get(args[0]);
        SubjectMigration app = new SubjectMigration();
        try {
            app.processInputFile(inPath);
        } catch (IOException ex) {
            LOGGER.error("Error while merging files", ex);
        }
    }

    private void processInputFile(Path inPath) throws IOException {
        LOGGER.info("Processing input file {}", inPath);
        Path outPath = inPath.getParent().resolve("results.csv");
        try (
                FileReader reader = new FileReader(inPath.toFile(), Charset.forName("utf-8"));
                FileWriter out = new FileWriter(outPath.toFile(), Charset.forName("utf-8"))) {
            CSVPrinter printer = CSVFormat.DEFAULT.withHeader("id", "dcSubject").print(out);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                Set<String> subjects = new HashSet<>();
                processIsadFonds(record, subjects);
                processIsadSeries(record, subjects);
                processIsadFile(record, subjects);
                if (!subjects.isEmpty()) {
                    dcSubject.addAll(subjects);
                    String values = subjectsToString(subjects);
                    printer.print(record.get("id"));
                    printer.print(values);
                    printer.println();
                }
            }
        }
        LOGGER.info("Results saved in file {}", outPath);
        outPath = inPath.getParent().resolve("isadFonds.txt");
        Files.write(outPath, isadFonds, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        outPath = inPath.getParent().resolve("isadSeries.txt");
        Files.write(outPath, isadSeries, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        outPath = inPath.getParent().resolve("isadFile.txt");
        Files.write(outPath, isadFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        outPath = inPath.getParent().resolve("dcSubject.txt");
        Files.write(outPath, dcSubject, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        LOGGER.info("The operation completed");
    }

    private void processIsadFonds(CSVRecord record, Set<String> subjects) {
        String fonds = record.get("isad_fonds");
        if (fonds == null) {
            return;
        }
        fonds = fonds.trim();
        switch (fonds) {
            case "חינוך":
            case "תרבות":
            case "ענפים":
            case "הנצחה":
            case "חקלאות":
            case "ענפי שירות":
            case "בריאות":
            case "ספורט":
            case "נוסטלגיה":
            case "תערוכת הנוסטלגיה":
            case "פוליטיקה":
            case "אבלות":
            case "מגזר צרכני":
                subjects.add(fonds);
                break;
            case "תרובת":
                subjects.add("תרבות");
                break;
            case "חירום וביטחון":
            case "חירום ובטחון":
            case "חרום ובטחון":
            case "בטחון":
            case "ביטחון":
            case "חירום":
                subjects.add("חירום וביטחון");
                break;
            case "חינןך":
                subjects.add("חינוך");
                break;
            case "ספורט- הנצחה":
                subjects.add("ספורט");
                subjects.add("הנצחה");
                break;
            case "תרבות, הנצחה":
                subjects.add("תרבות");
                subjects.add("הנצחה");
                break;
            case "הגיל השלישי":
            case "גיל שלישי":
                subjects.add("הגיל השלישי");
            default:
                isadFonds.add(fonds);
        }
    }

    private void processIsadSeries(CSVRecord record, Set<String> subjects) {
        String series = record.get("isad_series");
        if (series == null) {
            return;
        }
        series = series.trim();
        switch (series) {
            case "חגים":
            case "בר מצווה":
            case "חג מחזור":
            case "מנהלת מש\"א":
            case "בית ספר":
            case "אירועים":
            case "צח\"י":
            case "יסודי":
            case "פורים":
            case "במבוק":
            case "צבעוני":
            case "שבועות":
            case "פסח":
            case "כתת שקד":
            case "סוכות":
            case "בתים פתוחים":
            case "כתת נופר":
            case "חנוכה":
            case "ועדה כלכלית":
            case "בתי ילדים":
            case "ערוץ 900":
            case "לקראת מזכירות":
            case "נעורים":
            case "ועדות":
            case "קפה בריכה":
            case "חקלאות":
            case "בית ספר נופים":
            case "הצגות הפקה מקומית":
            case "מבנים":
            case "ספורט":
            case "מפגשי שיח חברים":
            case "בנין":
            case "חצר הקיבוץ":
            case "חטיבה":
            case "מחזורים":
            case "מועדון יום ד'":
            case "לונה גל":
            case "פרדס":
            case "תרבות":
            case "דיר":
            case "לילות שבת":
            case "תקנונים":
            case "שרברבות":
            case "מודעות":
            case "תיעוד":
            case "חינוך":
            case "ערבי תרבות":
            case "רפת":
            case "שנת שירות":
            case "הרצאות":
            case "פלחה":
            case "נוי":
            case "מכוניות":
            case "בניינים וחצר":
            case "רווחה":
            case "מטבח":
            case "ענפים":
            case "קיבוץ לומד":
            case "בית תינוקות":
            case "תכנון ובניה":
            case "כתת לימוד":
            case "הודעות לציבור":
            case "שיכון":
            case "יום האישה":
            case "ערב הוקרה":
            case "בית ספר יסודי":
            case "חינוך משלים":
            case "קלפי":
            case "חצר":
            case "הצגות":
            case "חוגים":
            case "מוסך":
            case "תרומת דם":
            case "הנצחה":
            case "ועדת תכנון":
            case "ועדת ביקורת":
            case "פתיחת שנת הלימודים":
            case "עלוני בית ספר":
            case "משאבי אנוש":
            case "בריכה":
            case "שנת בר מצווה":
            case "סרטים":
            case "דיון ציבורי":
            case "בארי בריא":
            case "ענף המזון":
            case "פאב":
            case "הגיל השלישי":
            case "חברים":
            case "יום כיפור":
            case "גרעינים והכשרות":
            case "ועדת בנים":
            case "מוזיקה":
                subjects.add(series);
                break;
            case "פעילות זוגונים":
                subjects.add("פעילות בזוגונים");
                break;
            case "נעורים חג מחזור":
                subjects.add("נעורים");
                subjects.add("חג מחזור");
                break;
            case "נוהלים":
                subjects.add("נהלים");
                break;
            case "קיבוץלומד":
                subjects.add("קיבוץ לומד");
                break;
            case "יום הזיכרון לחללי צה\"ל":
                subjects.add("יום הזיכרון");
                break;
            case "חגי מחזור":
                subjects.add("חג מחזור");
                break;
            case "גיל רך גנים":
            case "גיל רך":
            case "גנים":
            case "גרג":
                subjects.add("גיל רך גנים");
                break;
            case "ארועים":
                subjects.add("אירועים");
                break;
            case "חגים - עצמאות":
                subjects.add("עצמאות");
                subjects.add("חגים");
                break;
            case "ענפים - דפוס":
                subjects.add("דפוס");
                subjects.add("ענפים");
                break;
            case "דפוס":
            case "דפוס בארי":
                subjects.add("דפוס");
                break;
            case "חנוכה - חגים":
                subjects.add("חגים");
                subjects.add("חנוכה");
                break;
            case "חגים - פורים ילדים":
                subjects.add("פורים");
                subjects.add("ילדים");
                subjects.add("חגים");
                break;
            case "ענפים פלחה":
                subjects.add("ענפים");
                subjects.add("פלחה");
                break;
            case "חגים - יום העצמאות":
            case "חגים יום העצמאות":
                subjects.add("יום העצמאות");
                subjects.add("חגים");
                break;
            case "חתונות, כלולות,":
            case "חתונות , כלולות":
            case "חתונות, כלולות":
                subjects.add("חתונות");
                break;
            case "חגים - חג העומר":
                subjects.add("חג העומר");
                subjects.add("חגים");
                break;
            case "תרבות - חברים":
                subjects.add("תרבות");
                break;
            case "חגים- מועדים":
                subjects.add("חגים");
                break;
            case "חגים - חנוכה":
                subjects.add("חנוכה");
                subjects.add("חגים");
                break;
            case "חגים - פורים":
                subjects.add("פורים");
                subjects.add("חגים");
                break;
            case "חגים - פסח":
                subjects.add("פסח");
                subjects.add("חגים");
                break;
            case "חינוך מחזורים":
                subjects.add("חינוך");
                subjects.add("מחזורים");
                break;
            case "מחזורים - בר מצווה":
                subjects.add("בר מצווה");
                subjects.add("מחזורים");
                break;
            case "חגים - טו בשבט":
                subjects.add("טו בשבט");
                subjects.add("חגים");
                break;
            case "חגים - ראש השנה":
                subjects.add("ראש השנה");
                subjects.add("חגים");
                break;
            case "חגים ומועדים":
                subjects.add("חגים");
                break;
            case "חגים - סוכות":
                subjects.add("סוכות");
                subjects.add("חגים");
                break;
            case "חגים - שבועות":
                subjects.add("שבועות");
                subjects.add("חגים");
                break;
            case "חגים חנוכה":
                subjects.add("חנוכה");
                subjects.add("חגים");
                break;
            case "חגים פורים":
                subjects.add("פורים");
                subjects.add("חגים");
                break;
            case "ענפים דפוס":
                subjects.add("ענפים");
                subjects.add("דפוס");
                break;
            case "ענפים - פלחה":
                subjects.add("ענפים");
                subjects.add("פלחה");
                break;
            case "חקלאות, פלחה":
                subjects.add("פלחה");
                subjects.add("חקלאות");
                break;
            case "גלריה בארי":
            case "גלריה":
                subjects.add("גלריה");
                break;
            default:
                isadSeries.add(series);
        }
    }

    private void processIsadFile(CSVRecord record, Set<String> subjects) {
        String file = record.get("isad_file");
        if (file == null) {
            return;
        }
        file = file.trim();
        switch (file) {
            case "אבוקדו":
            case "אגף חברה":
            case "אולימפיאדה":
            case "ארכיאולוגיה":
            case "בארי 2025":
            case "בית ספר":
            case "במבוק":
            case "בנין":
            case "בריכה":
            case "בר מצווה":
            case "בתים פתוחים":
            case "גגות אסבסט":
            case "גיוסים":
            case "גלריה":
            case "גן חצב":
            case "גרנוליט":
            case "גרעין אביר":
            case "גרעין תל גב":
            case "דברי ערך":
            case "דפוס":
            case "האחד במאי":
            case "הינומן":
            case "הכל תקין":
            case "הצגות":
            case "הרצאות":
            case "ועדת אבלות":
            case "ועדת ביקורת":
            case "ועדת בריאות":
            case "ועדת חברה":
            case "ועדת חינוך":
            case "ועדת מינויים":
            case "ועדת קליטה":
            case "ועדת שיכון":
            case "ועדת תכנון":
            case "חברה":
            case "חג המשק":
            case "חג הסיגד":
            case "חג העומר":
            case "חגים":
            case "חג מחזור":
            case "חדר אוכל":
            case "חוגים":
            case "חופש גדול":
            case "חטיבה":
            case "חטיבת בני בארי":
            case "חינוך":
            case "חנוכה":
            case "חצר הקיבוץ":
            case "חתונות":
            case "טיולים":
            case "טפסים":
            case "יום הגבר":
            case "יום הילד":
            case "יום העצמאות":
            case "יום כיפור":
            case "כדורסל":
            case "כיתת גפן":
            case "כיתת דגן":
            case "כיתת דקל":
            case "כיתת דרור":
            case "כיתת הדס":
            case "כרטיסי שנה טובה":
            case "כתת איריס":
            case "כתת אלה":
            case "כתת אלון":
            case "כתת אננס":
            case "כתת ארז":
            case "כתת אשל":
            case "כתת במבוק":
            case "כתת גפן":
            case "כתת דגן":
            case "כתת דקל":
            case "כתת הדס":
            case "כתת הדר":
            case "כתת זית":
            case "כתת חמנית":
            case "כתת חרוב":
            case "כתת חרצית":
            case "כתת לבנה":
            case "כתת מרוה":
            case "כתת נופר":
            case "כתת נורית":
            case "כתת נרקיס":
            case "כתת סביון":
            case "כתת עומר":
            case "כתת עירית":
            case "כתת ערבה":
            case "כתת פיסטוק":
            case "כתת פלפל":
            case "כתת פקאן":
            case "כתת צאלון":
            case "כתת צבעוני":
            case "כתת צבר":
            case "כתת רותם":
            case "כתת רימון":
            case "כתת רקפת":
            case "כתת שיבולים":
            case "כתת שיזף":
            case "כתת שיטה":
            case "כתת שקד":
            case "כתת תאנה":
            case "כתת תומר":
            case "כתת תומר שיזף":
            case "כתת תות":
            case "כתת תפוז":
            case "לג בעומר":
            case "לוגו כיתות":
            case "לונה גל":
            case "לימון":
            case "מחזורים":
            case "מכבסה":
            case "מנהלת מש\"א":
            case "מעורבות חברתית":
            case "מקלטים":
            case "מרכולית":
            case "מש\"א":
            case "משאבי אנוש":
            case "משק ילדים":
            case "נאות בארי":
            case "נאחביר":
            case "נהלים":
            case "ניהול":
            case "ניצני בארי":
            case "נשקיה":
            case "סוכות":
            case "סיגד":
            case "ספורט":
            case "ספר הג'ונגל":
            case "ספרי בארי":
            case "ספריה":
            case "עבודת חוץ":
            case "עובדי חוץ":
            case "עובדים שכירים":
            case "עומר":
            case "עופרת יצוקה":
            case "עיונה":
            case "עירית":
            case "עליה לכתה א'":
            case "ענף הבגד":
            case "ענף הנוי":
            case "ערבי תרבות":
            case "ערב לביבות חנוכה":
            case "פאב":
            case "פורים":
            case "פי הבאר":
            case "פסח":
            case "פסח העומר":
            case "פסטיבל שירי ילדים":
            case "פעילות בזוגונים":
            case "פרדס":
            case "פרוייקט סיפורי חיים":
            case "פרויקטים":
            case "פרלמנט העמק":
            case "פתיחת שנת הלימודים":
            case "צבעוני":
            case "צחי":
            case "צמ\"ח":
            case "קבלת שבת":
            case "קוקוס":
            case "קפה בריכה":
            case "רביעית בארי":
            case "רווחה":
            case "רכב":
            case "רכב תאונה":
            case "רפת":
            case "שבוע החטיבה":
            case "שבועות":
            case "שבועות ביכורים":
            case "שועל":
            case "שיכון":
            case "שנת בר מצווה":
            case "שנת ה-70":
            case "שנת ה 70 לבארי - שבוע נוסטלגיה":
            case "שנת השבעים":
            case "שנת שירות":
            case "שריפות":
            case "תוספתן":
            case "תורנויות":
            case "תכנון":
            case "תכניות אב":
            case "תכנית ברלה":
            case "תכנית השקעות":
            case "תמונה קבוצתית":
            case "תמונות מחזור":
            case "תמונת מחזור":
            case "תערוכת הנוסטלגיה":
            case "תערוכת נוסטלגיה":
            case "תערוכת שחזור תמונות היסטוריות":
            case "תקנונים":
            case "תקשורת":
            case "טו בשבט":
                subjects.add(file);
                break;
            case "ספרי זיכרון":
            case "ספרי זכרון":
                subjects.add("סיפרי זיכרון");
                break;
            case "סוכות עפיפוניאדה":
                subjects.add("סוכות");
                subjects.add("עפיפוניאדה");
                break;
            case "תריאטלון, ספורט":
            case "טריאתלון ספורט":
            case "טריאתלון":
                subjects.add("ספורט");
                subjects.add("טריאתלון");
                break;
            case "ראש השנה":
            case "ראש השנה 2018":
                subjects.add("ראש השנה");
                break;
            case "קיבוץ לומד":
            case "קיבוץ לומד 2004":
                subjects.add("קיבוץ לומד");
                break;
            case "קומונה":
            case "קומונה ה":
                subjects.add("קומונה");
                break;
            case "נוהלים":
                subjects.add("נהלים");
                break;
            case "מש\"א, עב\"ח":
                subjects.add("מש\"א");
                subjects.add("עב\"ח");
                break;
            case "כתת נענע חג מחזור":
                subjects.add("חג מחזור");
                subjects.add("כתת נענע");
                break;
            case "כלולות (חתונות)":
                subjects.add("חתונות");
                break;
            case "יום השואה":
            case "יום השואה והגבורה":
                subjects.add("יום השואה");
                break;
            case "יום הזיכרון":
            case "יום הזכרון":
            case "יום הזכרון לחללי צהל":
                subjects.add("יום הזיכרון");
                break;
            case "בר מצווה כתת ורד":
                subjects.add("בר מצווה");
                subjects.add("כתת ורד");
                break;
            case "בר מצווה כתת מנגו":
                subjects.add("בר מצווה");
                subjects.add("כתת מנגו");
                break;
            case "בר מצווה כתת נופר":
                subjects.add("בר מצווה");
                subjects.add("כתת נופר");
                break;
            case "בר מצווה כתת נענע":
                subjects.add("בר מצווה");
                subjects.add("כתת נענע");
                break;
            case "בר מצווה כתת פלפל":
                subjects.add("בר מצווה");
                subjects.add("כתת פלפל");
                break;
            case "בר מצווה כתת קוקוס":
                subjects.add("בר מצווה");
                subjects.add("כתת קוקוס");
                break;
            case "בר מצווה פלפל":
                subjects.add("בר מצווה");
                subjects.add("כתת פלפל");
                break;
            case "גיל רך גנים":
            case "גרג":
                subjects.add("גיל רך גנים");
                break;
            case "חג המשק, חג ה 30":
            case "חג 70":
            case "חג ה 30":
            case "חג ה 40":
            case "חג ה-50":
            case "חג ה-60":
            case "חג ה-61":
            case "חג ה70":
            case "חג ה 70":
            case "חג השבעים":
            case "חג השישים":
                subjects.add("חג המשק");
                break;
            case "חג מחזור כתת נענע":
                subjects.add("חג מחזור");
                subjects.add("כתת נענע");
                break;
            case "גיל שלישי":
                subjects.add("הגיל השלישי");
                break;
            case "טו באב":
            case "ט\"ו באב 2018":
                subjects.add("טו באב");
                break;
            case "טרור העפיפונים":
            case "טרור העפיפונים בן קינג יוני 2018":
            case "טרור עפיפונים":
                subjects.add("טרור העפיפונים");
                break;
            default:
                isadFile.add(file);
        }
    }

    private String subjectsToString(Set<String> subjects) {
        Object[] objects = subjects.toArray();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            builder.append(objects[i]);
            if (i < objects.length - 1) {
                builder.append(",");
            }
        }
        return builder.toString();

    }
}
