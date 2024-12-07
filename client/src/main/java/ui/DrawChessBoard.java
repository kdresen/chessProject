package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    private DrawChessBoard() {
    }

    public static void drawBoard(ChessBoard board, ChessGame.TeamColor playerColor,
                                 Collection<ChessPosition> moves) {
        boolean whitePlayer = (playerColor == ChessGame.TeamColor.WHITE);
        String[] colLabels = {"\u2003A ", "\u2002B ", "\u2003C ", "\u2003D ", "\u2003E ",
                "\u2003F ", "\u2003G ", "\u2003H "};
        System.out.print("Drawing chess board");

        System.out.println(ERASE_SCREEN);

        int startRow = whitePlayer ? 7 : 0;
        int endRow = whitePlayer ? 0 : 7;
        int rowStep = whitePlayer ? -1 : 1;
        int startCol = whitePlayer ? 0 : 7;
        int endCol = whitePlayer ? 7 : 0;
        int colStep = whitePlayer ? 1 : -1;


        System.out.print(SET_BG_COLOR_DARK_GREY + EMPTY);
        for (int i = startCol; i - colStep != endCol; i += colStep) {
            System.out.print(colLabels[i]);
        }
        System.out.println(EMPTY + RESET_BG_COLOR);
        for (int i = startRow; i - rowStep != endRow; i += rowStep) {
            System.out.print(SET_BG_COLOR_DARK_GREY + "\u2003" + (i + 1) + " ");
            if (i % 2 == 0) {
                for (int j = startCol; j - colStep != endCol; j += colStep) {
                    String backgroundColor = (j % 2 == 0) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                    if (moves != null && moves.contains(new ChessPosition(i + 1, j + 1))) {
                        backgroundColor = (j % 2 == 0) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN;
                    }
                    System.out.print(backgroundColor + getPieceString(board.getPiece(new ChessPosition(i + 1, j + 1))));
                }
            } else {
                for (int j = startCol; j - colStep != endCol; j += colStep) {
                    String backgroundColor = (j % 2 != 0) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                    if (moves != null && moves.contains(new ChessPosition(i + 1, j + 1))) {
                        backgroundColor = (j % 2 != 0) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN;
                    }
                    System.out.print(backgroundColor + getPieceString(board.getPiece(new ChessPosition(i + 1, j + 1))));
                }
            }
            System.out.println(SET_BG_COLOR_DARK_GREY + " " + (i + 1) + "\u2003" + RESET_BG_COLOR);
        }
        System.out.print(SET_BG_COLOR_DARK_GREY + EMPTY);
        for (int i = startCol; i - colStep != endCol; i += colStep) {
            System.out.print(colLabels[i]);
        }
        System.out.println(EMPTY + RESET_BG_COLOR);

    }

    public static void printHightlights(ChessBoard board, ChessGame.TeamColor playerColor, Collection<ChessMove> moves) {
        if (moves != null) {
            Collection<ChessPosition> positions = new ArrayList<>();
            for (ChessMove move : moves) {
                positions.add(move.getStartPosition());
                positions.add(move.getEndPosition());
            }

            drawBoard(board, playerColor, positions);
        } else {
            System.out.println("no moves available");
        }
    }

    private static String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            return switch (piece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case KNIGHT -> BLACK_KNIGHT;
                case BISHOP -> BLACK_BISHOP;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        } else {
            return switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case KNIGHT -> WHITE_KNIGHT;
                case BISHOP -> WHITE_BISHOP;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
        }
    }
}


