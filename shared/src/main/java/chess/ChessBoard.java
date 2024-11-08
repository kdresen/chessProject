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

    public ChessBoard copy() {
        ChessBoard copy = new ChessBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardPieces[i][j] != null) {
                    copy.boardPieces[i][j] = boardPieces[i][j].copy();
                }
            }
        }
        return copy;
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

    public ChessPiece getPiece(int row, int col) {
        row = row - 1;
        col = col - 1;

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

        ChessPiece.PieceType[] majorPieces = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK
        };

        setupMajorPieces(1, ChessGame.TeamColor.WHITE, majorPieces);
        setupPawns(2, ChessGame.TeamColor.WHITE);

        setupMajorPieces(8, ChessGame.TeamColor.BLACK, majorPieces);
        setupPawns(7, ChessGame.TeamColor.BLACK);
    }

    void setupMajorPieces(int row, ChessGame.TeamColor color, ChessPiece.PieceType[] majorPieces) {
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(row, col), new ChessPiece(color, majorPieces[col - 1]));
        }
    }

    void setupPawns(int row, ChessGame.TeamColor color) {
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(boardPieces, that.boardPieces);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardPieces);
    }
}
