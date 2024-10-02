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
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece clonedPiece = clonedBoard.getPiece(startPosition);

            if (move.getPromotionPiece() != null) {
                clonedPiece = new ChessPiece(clonedPiece.getTeamColor(), move.getPromotionPiece());
            }

            // move the piece on cloned board
            clonedBoard.addPiece(endPosition, clonedPiece);
            clonedBoard.addPiece(startPosition, null);

            if (!isInCheck(clonedPiece.getTeamColor())) {
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
         ChessPosition startPosition = move.getStartPosition();
         ChessPosition endPosition = move.getEndPosition();
         ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

         ChessPiece currentPiece = currentBoard.getPiece(startPosition);
         if (currentPiece == null) {
             throw new InvalidMoveException();
         }
         TeamColor pieceTeam = currentPiece.getTeamColor();

         if (teamTurn == pieceTeam) {
             Collection<ChessMove> validMoves = validMoves(startPosition);
             boolean isValidMove = false;
             for (ChessMove newMove : validMoves) {
                 if (newMove.equals(move)) {
                     isValidMove = true;
                     break;
                 }
             }

             if (isValidMove) {

                 if (promotionPiece != null) {
                     currentPiece.type = promotionPiece;
                 }

                 currentBoard.addPiece(endPosition, currentPiece);
                 currentBoard.addPiece(startPosition, null);

                 clonedBoard = currentBoard.copy();

                 teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
             } else {
                 throw new InvalidMoveException();
             }
         } else {
             throw new InvalidMoveException();
         }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        ChessPiece piece;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if (clonedBoard != null) {
                    piece = clonedBoard.getPiece(row, col);
                } else {
                    piece = currentBoard.getPiece(row, col);
                    clonedBoard = currentBoard.copy();
                }


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
        if (checkKing) {
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPiece piece = currentBoard.getPiece(row, col);

                    if (piece != null && piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> currentTeamMoves = validMoves(new ChessPosition(row, col));

                        if (!currentTeamMoves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean checkKing = isInCheck(teamColor);
        Collection<ChessMove> validMoves = new HashSet<>();

        for (int row = 1; row <=8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = currentBoard.getPiece(row, col);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    validMoves.addAll(validMoves(new ChessPosition(row, col)));
                }
            }
        }
        return !checkKing && validMoves.isEmpty();
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
        ChessPiece piece;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                if (clonedBoard != null) {
                    piece = clonedBoard.getPiece(row, col);
                } else {
                    piece = currentBoard.getPiece(row, col);
                }



                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(row, col);
                }
            }
        }
        return null;
    }
}
