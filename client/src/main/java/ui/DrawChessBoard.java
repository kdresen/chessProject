package ui;

import chess.ChessGame;
import chess.ChessPiece;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    public static final String WHITE_KING = " K ";
    public static final String WHITE_QUEEN = " Q ";
    public static final String WHITE_BISHOP = " B ";
    public static final String WHITE_KNIGHT = " K ";
    public static final String WHITE_ROOK = " R ";
    public static final String WHITE_PAWN = " P ";
    public static final String BLACK_KING = " K ";
    public static final String BLACK_QUEEN = " Q ";
    public static final String BLACK_BISHOP = " B ";
    public static final String BLACK_KNIGHT = " K ";
    public static final String BLACK_ROOK = " R ";
    public static final String BLACK_PAWN = " P ";
    public static final String EMPTY = "   ";


    private static ChessGame.TeamColor playerColor = null;
    private static ChessGame currentGame = null;

    ///  createChessBoard ChessGame game, ChessGame.TeamColor color

    public static void createChessBoard(ChessGame game, ChessGame.TeamColor color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        playerColor = color;
        currentGame = game;

        out.print(ERASE_SCREEN);

        drawHeaders(out);
        drawChessBoard(out);
        drawHeaders(out);

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void drawHeaders(PrintStream out) {
        setGray(out);
        out.print("   "); // padding for row numbers.
        if (playerColor == ChessGame.TeamColor.BLACK) {
            for (char file = 'H'; file >= 'A'; file--) {
                out.print(" ");
                out.print(file);
                out.print(" ");
            }
        } else {
            for (char file = 'A'; file <= 'H'; file++) {

                out.print(" ");
                out.print(file);
                out.print(" ");
            }
        }
        out.print("   ");
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void drawChessBoard(PrintStream out) {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; row++) {
            drawRow(out, row);
        }
    }

    private static void drawRow(PrintStream out, int boardRow) {
        // white perspective
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; squareRow++) {
            setGray(out);
            var rowNum =  playerColor == ChessGame.TeamColor.WHITE ? (8 - boardRow) : (1 + boardRow);
            out.print(" " + rowNum + " ");


            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
                if ((boardRow + boardCol) % 2 == 0) {
                    setWhite(out);
                } else {
                    setBlack(out);
                }

                String piece = getChessPiece(boardRow, boardCol, out);
                if (piece.equals(" ")) {
                    out.print(EMPTY);
                } else {
                    out.print(piece);
                }


            }
            setGray(out);
            rowNum = playerColor == ChessGame.TeamColor.WHITE ? (8 - boardRow) : (1 + boardRow);
            out.print(" " + rowNum + " ");

            out.print(RESET_BG_COLOR);
            out.println();
            setBlack(out);
        }
    }

    private static String getChessPiece(int row, int col, PrintStream out) {
        // I need to get the chess piece at the specific row and col,
        // but have it reversed if the playerColor
        // is black

        int adjustedRow = getAdjustedRow(row);
        int adjustedCol = getAdjustedCol(col);

        ChessPiece currentPiece = currentGame.getBoard().getPiece(adjustedRow + 1, adjustedCol + 1);
        if (currentPiece == null) {
            return EMPTY;
        }
        switch (currentPiece.getPieceType()) {
            case KING -> {
                if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    setRed(out);
                    return WHITE_KING;
                }
                setBlue(out);
                return BLACK_KING;
            }
            case QUEEN -> {
                if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    setRed(out);
                    return WHITE_QUEEN;
                }
                setBlue(out);
                return BLACK_QUEEN;
            }
            case BISHOP -> {
                if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    setRed(out);
                    return WHITE_BISHOP;
                }
                setBlue(out);
                return BLACK_BISHOP;
            }
            case KNIGHT -> {
                if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    setRed(out);
                    return WHITE_KNIGHT;
                }
                setBlue(out);
                return BLACK_KNIGHT;
            }
            case ROOK -> {
                if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    setRed(out);
                    return WHITE_ROOK;
                }
                setBlue(out);
                return BLACK_ROOK;
            }
            default -> {
                if (currentPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    setRed(out);
                    return WHITE_PAWN;
                }
                setBlue(out);
                return BLACK_PAWN;
            }
        }
    }

    private static int getAdjustedRow(int row) {
        if (playerColor == ChessGame.TeamColor.WHITE) {
            return BOARD_SIZE_IN_SQUARES - 1 - row;
        }
        return row;
    }

    private static int getAdjustedCol(int col) {
        if (playerColor == ChessGame.TeamColor.BLACK) {
            return BOARD_SIZE_IN_SQUARES - 1 - col;
        }
        return col;
    }


    // for next phase, some functions to highlight valid moves and such


    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_TEXT_COLOR_BLUE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
    }
}
