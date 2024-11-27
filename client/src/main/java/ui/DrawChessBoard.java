package ui;

import java.awt.desktop.AppReopenedEvent;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;

    private static final String EMPTY = "   ";
    private static final String X = " X ";
    private static final String O = " O ";

    private static final String[] WHITE_PIECES = {"R", "N", "B", "K", "Q", "B", "N", "R"};
    private static final String[] BLACK_PIECES = {"r", "n", "b", "k", "q", "b", "n", "r"};
    private static final String WHITE_PAWN = "P";
    private static final String BLACK_PAWN = "p";

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawHeaders(out);
        drawChessBoard(out);
        drawHeaders(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out) {
        setBlack(out);

        out.print(EMPTY); // padding for row numbers.
        out.print(SET_TEXT_COLOR_WHITE);
        for (char  file = 'A'; file <= 'H'; file++) {
            int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
            int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

            out.print(EMPTY.repeat(prefixLength));
            out.print(file);
            out.print(EMPTY.repeat(suffixLength));
        }
        out.print(EMPTY);
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void drawChessBoard(PrintStream out) {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            drawRow(out, row);
        }
    }

    private static void drawRow(PrintStream out, int boardRow) {
        setBlack(out);
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; squareRow++) {
            if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                out.print(" " + (8 - boardRow) + " ");
            } else {
                out.print(EMPTY);
            }

            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
                if ((boardRow + boardCol) % 2 == 0) {
                    setWhite(out);
                } else {
                    setBlack(out);
                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    out.print(EMPTY);
                    String piece = getChessPiece(boardRow, boardCol);
                    out.print(piece);
                    out.print(EMPTY);
                } else {
                    out.print(EMPTY);
                    out.print(" ");
                    out.print(EMPTY);
                }
            }

            if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                setBlack(out);
                out.print(" " + (8 - boardRow) + " ");
            } else {
                setBlack(out);
                out.print(EMPTY);
            }

            out.print(RESET_BG_COLOR);
            out.println();
            setBlack(out);
        }
    }
    ///
    /// For now this just does a starter chess board, I will modify this to use
    /// the ChessGame data to draw the board each time
    ///

    private static String getChessPiece(int row, int col) {
        if (row == 0) return BLACK_PIECES[col];
        if (row == 1) return BLACK_PAWN;

        if (row == 6) return WHITE_PAWN;
        if (row == 7) return WHITE_PIECES[col];


        return " ";
    }

    public static void testingStuff(PrintStream out) {
        int x, y;
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES - 1; row++) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES - 1; col++) {
                x = row * 20;

                y = col * 20;

                if ((row % 2 == 0) == (col % 2 == 0)) {
                    out.print(SET_BG_COLOR_BLACK);
                } else {
                    out.print(SET_BG_COLOR_WHITE);
                }


            }
        }
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
