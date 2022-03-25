package com.sophimp.are

import org.htmlcleaner.HtmlCleaner
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun parseTableHtml() {
        val tableHtml = "<table style=\"border-collapse: collapse; width: 62.5589%; height: 453px;\" border=\"1\" data-source=\"sgx\">\n" +
                "  <tbody>\n" +
                "    <tr style=\"height: 42px;\">\n" +
                "      <td style=\"width: 12.4898%; height: 42px; text-align: center;\" width=\"64\" height=\"24\">班別</td>\n" +
                "      <td style=\"width: 12.4898%; height: 42px; text-align: center;\" width=\"64\">区別</td>\n" +
                "      <td style=\"width: 12.4898%; height: 42px; text-align: center;\" width=\"64\">項目</td>\n" +
                "      <td style=\"width: 12.4898%; height: 42px; text-align: center;\" width=\"79\">開始時間</td>\n" +
                "      <td style=\"width: 12.4898%; height: 42px; text-align: center;\" width=\"29\">～</td>\n" +
                "      <td style=\"width: 12.4898%; height: 42px; text-align: center;\" width=\"87\">終了時間</td>\n" +
                "      <td style=\"width: 12.4898%; height: 42px; text-align: center;\" width=\"114\">合計時間(分間)</td>\n" +
                "      <td style=\"width: 16.6531%; height: 42px; text-align: center;\" width=\"191\">備考(0.5日休憩の場合)</td></tr>\n" +
                "    <tr style=\"height: 21px;\">\n" +
                "      <td style=\"width: 12.4898%; height: 204px; text-align: center;\" rowspan=\"9\" width=\"64\" height=\"216\">白班</td>\n" +
                "      <td style=\"width: 12.4898%; height: 69px; text-align: center;\" rowspan=\"3\" width=\"64\">前半</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\" width=\"64\">稼働</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\" width=\"79\">10:30</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\" width=\"29\">～</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\" width=\"87\">12:30</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\" width=\"114\">120</td>\n" +
                "      <td style=\"width: 16.6531%; height: 69px; text-align: center;\" rowspan=\"3\" width=\"191\">前半出勤後半休み\n" +
                "        <br />退勤打刻時間：15:15以降</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\" height=\"24\">昼食</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">12:30</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">13:15</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">45</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\" height=\"24\">稼働</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">13:15</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">15:15</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">120</td></tr>\n" +
                "    <tr style=\"height: 21px;\">\n" +
                "      <td style=\"width: 12.4898%; height: 90px; text-align: center;\" rowspan=\"4\" height=\"96\">後半</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">休憩</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">15:15</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">15:25</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">10</td>\n" +
                "      <td style=\"width: 16.6531%; height: 90px; text-align: center;\" rowspan=\"4\" width=\"191\">前半休み後半出勤\n" +
                "        <br />出勤打刻時間：15:15以前</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\" height=\"24\">稼働</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">15:25</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">17:15</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">110</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\" height=\"24\">晩食</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">17:15</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">17:55</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">40</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\" height=\"24\">稼働</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">17:55</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">19:45</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">110</td></tr>\n" +
                "    <tr style=\"height: 21px;\">\n" +
                "      <td style=\"width: 12.4898%; height: 45px; text-align: center;\" rowspan=\"2\" height=\"48\">定時後</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">休憩</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">19:45</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">19:55</td>\n" +
                "      <td style=\"width: 12.4898%; height: 21px; text-align: center;\">10</td>\n" +
                "      <td style=\"width: 16.6531%; height: 45px; text-align: center;\" rowspan=\"2\" width=\"191\">-</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\" height=\"24\">残業</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">19:55</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">22:25</td>\n" +
                "      <td style=\"width: 12.4898%; height: 24px; text-align: center;\">150</td></tr>\n" +
                "    <tr style=\"height: 21px;\">\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 207px;\" rowspan=\"9\" width=\"64\" height=\"216\">夜班</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 69px;\" rowspan=\"3\" width=\"64\">前半</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\" width=\"64\">稼働</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\" width=\"79\">23:05</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\" width=\"29\">～</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\" width=\"87\">1:15</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\" width=\"114\">130</td>\n" +
                "      <td style=\"width: 16.6531%; text-align: center; height: 69px;\" rowspan=\"3\" width=\"191\">前半出勤後半休み\n" +
                "        <br />退勤打刻時間：4:00以降</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\" height=\"24\">深夜食</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">1:15</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">2:00</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">45</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\" height=\"24\">稼働</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">2:00</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">4:00</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">120</td></tr>\n" +
                "    <tr style=\"height: 21px;\">\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 93px;\" rowspan=\"4\" height=\"96\">後半</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\">休憩</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\">4:00</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\">4:10</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\">10</td>\n" +
                "      <td style=\"width: 16.6531%; text-align: center; height: 93px;\" rowspan=\"4\" width=\"191\">前半休み後半出勤：\n" +
                "        <br />出勤打刻時間：4:00以前</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\" height=\"24\">稼働</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">4:10</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">6:00</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">110</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\" height=\"24\">朝食</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">6:00</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">6:40</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">40</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\" height=\"24\">稼働</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">6:40</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">8:20</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">100</td></tr>\n" +
                "    <tr style=\"height: 21px;\">\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 45px;\" rowspan=\"2\" height=\"48\">定時後</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\">休憩</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\">8:20</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\">8:30</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 21px;\">10</td>\n" +
                "      <td style=\"width: 16.6531%; text-align: center; height: 45px;\" rowspan=\"2\" width=\"191\">-</td></tr>\n" +
                "    <tr style=\"height: 24px;\">\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\" height=\"24\">残業</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">8:30</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">～</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">10:30</td>\n" +
                "      <td style=\"width: 12.4898%; text-align: center; height: 24px;\">120</td></tr>\n" +
                "  </tbody>\n" +
                "</table>"
        val htmlCleaner = HtmlCleaner()
        val tagNode = htmlCleaner.clean(tableHtml)
        var rows = tagNode.getElementListByName("tr", true)
        rows.forEach { eachRow ->
            var cols = eachRow.getElementListByName("td", false)
            cols.forEach { eachCol ->
                var rowspanAttr = eachCol.getAttributeByName("rowspan")
                var colspanAttr = eachCol.getAttributeByName("colspan")
                var alignAttr = eachCol.getAttributeByName("text-align")
            }
        }

    }
}