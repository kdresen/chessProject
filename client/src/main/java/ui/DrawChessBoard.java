package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        printChessBoard(out);
    }

    public static void printChessBoard(PrintStream out) {
        String[] whitePieces = {"R", "N", "B", "K", "Q", "B", "N", "R"};
        String[] blackPieces = {"r", "n", "b", "k", "q", "b", "n", "r"};

        // print column label
        out.print("   ");
        for (char c = 'h'; c >= 'a'; c--) {
            out.print(c + " ");
        }
        out.println();

    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLUE);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }
}
