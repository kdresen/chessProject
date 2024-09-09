package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor pieceColor;
    ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new HashSet<>();

        // switch selection for each type of piece
        switch(this.type) {
            case KING:
                findKingMoves(board, myPosition, possibleMoves);
                break;
            case QUEEN:
                findQueenMoves(board, myPosition, possibleMoves);
                break;
            case BISHOP:
                findBishopMoves(board, myPosition, possibleMoves);
                break;
            case KNIGHT:
                findKnightMoves(board, myPosition, possibleMoves);
                break;
            case ROOK:
                findRookMoves(board, myPosition, possibleMoves);
                break;
            default:
                findPawnMoves(board, myPosition, possibleMoves);

        }
        return possibleMoves;
    }

    void findKingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        //TODO
    }

    void findQueenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        // TODO
    }

    void findBishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        // TODO
    }

    void findKnightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        // TODO
    }

    void findRookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        // TODO
    }

    void findPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        // TODO
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
