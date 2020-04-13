package util;

import org.apache.commons.lang3.CharUtils;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.SPACE;

public class NameMatching {
    public static MatchResult check(String name, String other, boolean isPersonal) {
        NameHolder nameHolder = new NameHolder(name);
        NameHolder otherHolder = new NameHolder(other);
        Matcher matcher = isPersonal ? new PersonalMatcher() : new CorpMatcher();
        return matcher.doMatch(nameHolder, otherHolder);
    }

    public synchronized void reloadCache() {
        CorpMatcher.loadNameMapping();
    }

    public static void main(String[] args) {
        System.out.println(check("lai wenqiang", "lai wenqiang", true));
        System.out.println(check("lai wenqiang", "wenqiang lai", true));
        System.out.println(check("MR lai wenqiang", "wenqiang lai", true));
        System.out.println(check("MR lai wenqiang先生", "wenqiang lai", true));
        System.out.println(check("MR lai **wenqiang先生", "wenqiang lai", true));
        System.out.println(check("DF limited", "DF ltd", false));
        System.out.println(check("(SZ)DF limited", "(SZ)DF ltd", false));
        System.out.println(check("[SZ]DF limited", "(SZ)DF ltd", false));
        System.out.println(check("[SZ] DF limited", "(SZ)*DF ltd", false));
    }

    interface Matcher {
        MatchResult doMatch(NameHolder holder, NameHolder other);
    }

    static abstract class AbstractMatcher implements Matcher {
        abstract boolean match0(NameHolder holder, NameHolder other);

        public MatchResult doMatch(NameHolder holder, NameHolder other) {
            if (holder.equals(other)) {
                return MatchResult.Y;
            }

            return match0(holder, other)
                    ? MatchResult.P
                    : MatchResult.N;
        }
    }

    static class PersonalMatcher extends AbstractMatcher {
        @Override
        boolean match0(NameHolder holder, NameHolder other) {
            System.out.println("Personal.. ");
            return holder.moveWord2Last().equals(other)
                    || holder.moveWord2Last().equals(other);
        }
    }

    static class CorpMatcher extends AbstractMatcher {
        private static Map<String, String> nameMapping = new HashMap<>();

        static {
            loadNameMapping();
        }

        @Override
        boolean match0(NameHolder holder, NameHolder other) {
            System.out.println("Corp...");
            String name = map(holder.name);
            String otherName = map(other.name);
            System.out.println("Name: " + name + ", otherName: " + otherName);
            return name.equalsIgnoreCase(otherName);
        }

        private String map(String name) {
            String name0 = SPACE + name.toUpperCase() + SPACE;
            for (String key : nameMapping.keySet()) {
                if (name0.contains(key)) {
                    String value = nameMapping.get(key);
                    name0 = name0.replace(key, value);
                }
            }
            return name0.trim();
        }

        public static void loadNameMapping() {
            // TODO, key 的前后加上空格
            nameMapping.put(" ADVERTISEMENT ", " ADV ");
            nameMapping.put(" ADVERTISING ", " ADV ");
            nameMapping.put(" AND ", " & ");
            nameMapping.put(" ASSOCIATION ", " ASSO ");
            nameMapping.put(" KOWLOON ", " KLN ");
            nameMapping.put(" LIMITED ", " LTD ");
        }
    }

    static class NameHolder {
        public boolean isEnglish = false;   // regard as english if contains an english letter.
        public String name;

        private static String[] TITLE_PREFIX = new String[]{"MR", "MRS", "MS", "MISS", "DR", "MR.", "MRS.", "DR."};
        private static String[] TITLE_SUFFIX = new String[]{"先生", "女士", "小姐"};
        private static String[][] symbolTable = new String[][]{
                {"(", "〔［[（"},
                {")", "〕］]）"},
                {"/", "／"},
                {"&", "＆"},
                {"'", "‘＇"},
                {SPACE, "　"}
        };
        private static int[][] chineseCharacterRange = new int[][] {
                {19968 , 40959 },   // 4E00-9FFF, CJK Unified Ideographs
                {13312 , 19903 },   // 3400-4DBF, CJK Unified Ideographs Extension A
                {131072, 173791},   // 20000-2A6DF, CJK Unified Ideographs Extension B
                {173824, 177983},   // 2A700-2B73F, CJK Unified Ideographs Extension C
                {177984, 178207},   // 2B740-2B81F, CJK Unified Ideographs Extension D
                {178208, 183983},   // 2B820-2CEAF, CJK Unified Ideographs Extension E
                {63744 , 64255 },   // F900-FAFF, CJK Compatibility Ideographs
                {194560, 195103}    // 2F800-2FA1F, CJK Compatibility Ideographs Supplement
        };

        public NameHolder(String name) {
            this.name = name.trim();    // trim it first!
            process();
        }

        public boolean equals(NameHolder other) {
            return this.name.equalsIgnoreCase(other.name);
        }

        public NameHolder moveWord2Last() {
            if (isEnglish) {
                int index = name.indexOf(SPACE);
                if (index != -1) {
                    name = name.substring(index + 1) + SPACE + name.substring(0, index);
                }
            } else {
                int cp = name.codePointAt(0);
                int index = 0;
                String c = Character.isSupplementaryCodePoint(cp)
                        ? name.substring(0, index += 2)
                        : name.substring(0, ++index);
                name = name.substring(index) + c;
            }
            return this;
        }

        private void process() {
            removeTitle();
            String last = "";
            StringBuffer buffer = new StringBuffer();
            for (int i = 0, j = 0; i < name.codePointCount(0, name.length()); i++) {
                int cp = name.codePointAt(j);
                String c = Character.isSupplementaryCodePoint(cp)
                        ? name.substring(j, j+=2)
                        : name.substring(j, ++j);

                if (!isEnglish && CharUtils.isAsciiAlpha(c.charAt(0))) {
                    isEnglish = true;
                }

                c = isChinese(cp) ? c : doMap(c, cp);

                if (SPACE.equals(c) && SPACE.equals(last)) {
                    continue;
                }

                buffer.append(c);
                last = c;
            }

            name = buffer.toString();

            if (!isEnglish) {
                name = name.replace(SPACE, "");
            }
        }

        private String doMap(String s, int cp) {
            if (Character.isLetterOrDigit(cp)) {
                return s;
            }

            for (String[] row : symbolTable) {
                String winner = row[0];
                String candidate = row[1];
                if (s.equals(winner) || candidate.contains(s)) {
                    return winner;
                }
            }
            return SPACE;
        }

        private boolean isChinese(int codePoint) {
            if (codePoint < 127) {
                return false;
            }

            for (int[] range : chineseCharacterRange) {
                int start = range[0];
                int end = range[1];
                if (codePoint >= start && codePoint <= end) {
                    return true;
                }
            }
            return false;
        }

        private void removeTitle() {
            for (String title : TITLE_PREFIX) {
                String title0 = title + SPACE;
                if (name.startsWith(title0)) {
                    name = name.substring(title0.length());
                    break;
                }
            }

            for (String title : TITLE_SUFFIX) {
                if (name.endsWith(title)) {
                    name = name.substring(0, name.length() - title.length());
                    break;
                }
            }
        }
    }
}

enum MatchResult {
    Y, P, N;

    public boolean isSuccess() {
        return !N.equals(this);
    }
}






























