package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    public ChessPiece[][] boardPieces;
    public ChessBoard() {
        boardPieces = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow() - 1;
        int col = position.getColumn() - 1;

        if (isValidPosition(row, col)) {
            boardPieces[row][col] = piece;
        } else {
            throw new IllegalArgumentException("Invalid position");
        }
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow() - 1;
        int col = position.getColumn() - 1;

        if (isValidPosition(row, col)) {
            return boardPieces[row][col];
        } else {
            throw new IllegalArgumentException("Invalid position");
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // clear board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardPieces[i][j] = null;
            }
        }

        // set new pieces for board
        // TODO
    }
}
