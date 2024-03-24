package moe.nero.unpacker

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UnpackerTest {
    @Test
    fun testDetect() {
        assertFalse(Unpacker.detect(""))
        assertFalse(Unpacker.detect("var a = b"))
        assertTrue(Unpacker.detect("eval(function(p,a,c,k,e,r)"))
        assertTrue(Unpacker.detect("eval ( function(p, a, c, k, e, r"))
    }

    @Test
    fun testUnpack() {
        fun check(input: String, output: String) {
            assertEquals(output, Unpacker.unpack(input))
        }

        check(
            """
                eval(function(p,a,c,k,e,r){e=String;if(!''.replace(/^/,String)
                ){while(c--)r[c]=k[c]||c;k=[function(e){return r[e]}];e=
                function(){return'\\\\w+'};c=1};while(c--)if(k[c])p=p.replace(
                new RegExp('\\\\b'+e(c)+'\\\\b','g'),k[c]);return p}('0 2=1',62,3,'var||a'.split('|'),0,{}))
            """.trimIndent(),
            "var a=1"
        )

        check(
            "function test (){alert ('This is a test!')}; " +
                    "eval(function(p,a,c,k,e,r){e=String;if(!''.replace(/^/,String))" +
                    "{while(c--)r[c]=k[c]||c;k=[function(e){return r[e]}];e=function" +
                    "(){return'\\w+'};c=1};while(c--)if(k[c])p=p.replace(Regex(" +
                    "'\\b'+e(c)+'\\b'),'g'),k[c]);return p}('0 2=\\'{Íâ–+›ï;ã†Ù¥#\\'',3,3," +
                    "'var||a'.split('|'),0,{}))",
            "function test (){alert ('This is a test!')}; var a='{Íâ–+›ï;ã†Ù¥#'",
        )

        check(
            "eval(function(p,a,c,k,e,d){e=function(c){return c.toString(36)};if(!''.replace(/^/,String)){while(c--){d[c.toString(a)]=k[c]||c.toString(a)}k=[function(e){return d[e]}];e=function(){return'\\w+'};c=1};while(c--){if(k[c]){p=p.replace(Regex('\\b'+e(c)+'\\b'),'g'),k[c])}}return p}('2 0=\"4 3!\";2 1=0.5(/b/6);a.9(\"8\").7=1;',12,12,'str|n|var|W3Schools|Visit|search|i|innerHTML|demo|getElementById|document|w3Schools'.split('|'),0,{}))",
            "var str=\"Visit W3Schools!\";var n=str.search(/w3Schools/i);document.getElementById(\"demo\").innerHTML=n;",
        )

        check(
            "a=b;\r\nwhile(1){\ng=h;{return'\\w+'};break;eval(function(p,a,c,k,e,d){e=function(c){return c.toString(36)};if(!''.replace(/^/,String)){while(c--){d[c.toString(a)]=k[c]||c.toString(a)}k=[function(e){return d[e]}];e=function(){return'\\w+'};c=1};while(c--){if(k[c]){p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c])}}return p}('$(5).4(3(){$('.1').0(2);$('.6').0(d);$('.7').0(b);$('.a').0(8);$('.9').0(c)});',14,14,'html|r5e57|8080|function|ready|document|r1655|rc15b|8888|r39b0|r6ae9|3128|65309|80'.split('|'),0,{}))c=abx;",
            "a=b;\r\nwhile(1){\ng=h;{return'\\w+'};break;$(document).ready(function(){$('.r5e57').html(8080);$('.r1655').html(80);$('.rc15b').html(3128);$('.r6ae9').html(8888);$('.r39b0').html(65309)});c=abx;",
        )
    }
}
