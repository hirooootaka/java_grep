package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ディレクトリ再帰ファイルGREPプログラム Java版
 * <p>
 * 引数
 * [0]:再帰ディレクトリパス
 * [1]:対象ファイル正規表現
 * <p>
 * ex. gradle run --args="/usr/local/Cellar .*\.md"
 */
public class Checker {

    /**
     * 検索対象ワード
     */
    public static final String[] TARGET_WORDS = {
            "This",
            "Check",
            "Just",
    };

    /**
     * 検索ワード有無Map
     */
    private static final Map<String, Boolean> wordExistMap = new HashMap<>();
    /**
     * RegexパターンMap
     */
    private static final Map<String, Pattern> regPatternMap = new HashMap<>();

    /**
     * main
     *
     * @param args [0]:path [1]:regex
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("args is [0]:path [1]:regex");
            System.exit(0);
        }

        long t1 = System.currentTimeMillis();
        System.out.println(t1);

        System.out.println("** init **");
        Checker chk = new Checker();
        List<String> words = Arrays.stream(TARGET_WORDS)
                .filter(v -> !v.equals("")).peek(word -> {
                    wordExistMap.put(word, false);
                    regPatternMap.put(word, Pattern.compile(String.format(".*%s.*", word)));
                }).collect(Collectors.toList());

        System.out.println("** start **");
        List<Path> paths = new ArrayList<>();

        Pattern reg = Pattern.compile(args[1]);
        chk.dirwalk(paths, new File(args[0]), reg);

        System.out.println("** regexp **");
        final Integer[] idx = {0};
        final Integer size = paths.size();
        paths.forEach(path -> {
            idx[0]++;
            //System.out.println(String.format("%d/%d", idx[0], size));
            List<String> r = findReg(path, words);
            if (!r.isEmpty()) {
                System.out.printf("%n%d/%d[%s]%n", idx[0], size, path);
                r.forEach(System.out::println);
            }
        });

        System.out.println("** result **");
        words.forEach(code -> {
            System.out.printf("%s: %b%n", code, wordExistMap.get(code));
        });

        long t2 = System.currentTimeMillis();
        System.out.println(t2);
        long t3 = TimeUnit.MILLISECONDS.toSeconds(t2 - t1);
        if (t3 > 60) {
            System.out.println(t3 / 60.0d + "min");
        } else {
            long t4 = TimeUnit.MILLISECONDS.toMillis(t2 - t1);
            System.out.println(t4 + "msec");
        }
    }

    /**
     * 対象ワード検索処理
     *
     * @param path
     * @param words
     * @return
     */
    private static List<String> findReg(Path path, List<String> words) {
        List<String> find = new ArrayList<>();
        try {
            final Integer[] num = {0};
            Files.lines(path, StandardCharsets.UTF_8).forEach(line -> {
                num[0]++;
                words.forEach(word -> {
                    if (regPatternMap.get(word).matcher(line).matches()) {
                        wordExistMap.put(word, true);
                        find.add(String.format("[%s](%d) %s", word, num[0], line));
                    }
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return find;
    }

    /**
     * ディレクトリ再帰処理
     *
     * @param paths
     * @param file
     * @param reg
     */
    private void dirwalk(List<Path> paths, File file, Pattern reg) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }

        for (File tmpFile : files) {
            if (tmpFile.isDirectory()) {
                dirwalk(paths, tmpFile, reg);
            } else {
                if (reg.matcher(tmpFile.getName()).matches()) {
                    paths.add(Paths.get(tmpFile.getAbsoluteFile().toString()));
                }
            }
        }
    }
}
