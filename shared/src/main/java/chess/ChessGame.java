package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard currentBoard;

    private ChessBoard clonedBoard;
    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        currentBoard = new ChessBoard();
        currentBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new HashSet<>();

        // start position
        ChessPiece piece = currentBoard.getPiece(startPosition);
        // get possible moves
        Collection<ChessMove> possibleMoves = piece.pieceMoves(currentBoard, startPosition);

        // check if the move puts friendly king in check
        for (ChessMove move : possibleMoves) {
            clonedBoard = currentBoard.copy();

            // make the move
            ChessPosition newPosition = move.getEndPosition();
            ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

            int row = startPosition.getRow() - 1;
            int col = startPosition.getColumn() - 1;
            int newRow = newPosition.getRow() - 1;
            int newCol = newPosition.getColumn() - 1;


            if (promotionPiece != null) {
                piece.type = promotionPiece;
            }

            // move the piece on cloned board
            clonedBoard.boardPieces[newRow][newCol] = piece;
            clonedBoard.boardPieces[row][col] = null;

            boolean isChecked = isInCheck(piece.getTeamColor());

            if (!isChecked) {
                validMoves.add(move);
            }

            // reset the clonedBoard
            clonedBoard = currentBoard.copy();
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);

        if (kingPosition == null) {
            throw new IllegalArgumentException("King not on board");
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = clonedBoard.getPiece(row, col);

                if (piece != null && piece.getTeamColor() != teamColor) {
                    // check if any of the other teams moves can threaten the king
                    for (ChessMove move : piece.pieceMoves(clonedBoard, row, col)) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean checkKing = isInCheck(teamColor);


        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // TODO king is not in check but team has no legal moves available

        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
        clonedBoard = currentBoard.copy();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }

    public ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = clonedBoard.getPiece(row, col);

                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(row, col);
                }
            }
        }
        return null;
    }
}
