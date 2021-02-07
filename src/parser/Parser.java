package parser;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static String removeContents(String source){
        StringBuilder tmpStr = new StringBuilder(source);
        Stack tmpIndex = new Stack();
        String patternStr = "(?<=\\))(\\s*\\{)";
        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(source);
        int start = 0;
        while (m.find(start)) {
            int temp = m.end();
            int temp2 = 0;
            tmpIndex.push(m.end() - 1 - (source.length() - tmpStr.length()));
            do {
                for (int i = (int) tmpIndex.peek() + 1; i < tmpStr.length(); i++) {
                    if ('{' == tmpStr.charAt(i)) {
                        tmpIndex.push(i);
                        continue;
                    }
                    if ('}' == tmpStr.charAt(i)) {
                        temp2 += i + 1 - (int) tmpIndex.peek() - 1;
                        tmpStr.replace((int) tmpIndex.pop(), i + 1, ";");
                        break;
                    }
                }
            } while (!tmpIndex.isEmpty());
            start = temp + temp2 - 1;
            if (start >= source.length())
                break;
        }
        return tmpStr.toString().trim();
    }

    public static String preprocess(String source) {
        source = source.replaceAll("@.*\n", " ");

        int begin = source.indexOf("{");
        int end = source.lastIndexOf("}");
        if (begin>-1 && end>-1) {
            source = source.substring(0, begin) + ";" + source.substring(begin+1, end);
        }
        //else System.out.println(source);

        while (source.contains("{"))
            source = source.replaceAll("\\{[^{^}]*\\};*", ";");

        source = source.replace("\n", " ");
        source = source.replaceAll("(import|package)[^;]+;", "");

        return source.trim();
    }


    public static String normalize(String source) {
        source = source.replaceAll("(['\"])(?:\\.|" + "(?!\1).)*\1|/\\*(?:.|[\\n\\r])*?\\*/|(?://.*)","")  // remove comments
                        .replaceAll("=\\s?.*(?=;)","")  // remove value of variables
                        .replaceAll("\".*\"","")  // remove quotes
                        .replaceAll("\\s{2,}"," ")  // remoce white space
                        .replaceAll("(?!\\w+)\\s(?=\\()","")  // standardize source
                        .replaceAll("\\](?=\\w)","\\] ")
                        .replaceAll(">(?=\\w)","> ")
                        .replaceAll("(?!\\w+)\\s(?=\\()","") // fix source
                        .replaceAll(">(?=\\w)","> ")
                        .replaceAll(",\\s(?=\\w)",",");
        source = removeContents(source);
        source = preprocess(source);
        return source.trim();
    }

    public static String[] getClassDeclaration(String source) {
        source = normalize(source);
        String[] res = new String[3];
        String[] tmp = source.split(";")[0].replaceAll("(\\bextends\\b)|(\\bimplements\\b)", "-").split("-");
        res[0] = tmp[0].trim();
        if (tmp.length == 1) return res;

        if (tmp.length == 2)
            if (source.matches(".+ extends .+"))
                res[1] = tmp[1].trim();
            else
                res[2] = tmp[1].substring(0,tmp[1].indexOf(';')).trim().replace(",", " ");
        else {
            res[1] = tmp[1].trim();
            res[2] = tmp[2].trim().replace(",", " ");
        }

        return res;
    }
}