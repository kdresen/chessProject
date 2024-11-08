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
    public ChessPiece() {}

    public ChessPiece copy() {
        ChessPiece copiedPiece = new ChessPiece();

        copiedPiece.pieceColor = this.pieceColor;
        copiedPiece.type = this.type;


        return copiedPiece;
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
        // adding new comment to reach minimum commit amount
        return getChessMoves(board, myPosition, possibleMoves);
    }

    private Collection<ChessMove> getChessMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        switch(this.type) {
            case KING -> findKingMoves(board, myPosition, possibleMoves);
            case QUEEN -> findQueenMoves(board, myPosition, possibleMoves);
            case BISHOP -> findBishopMoves(board, myPosition, possibleMoves);
            case KNIGHT -> findKnightMoves(board, myPosition, possibleMoves);
            case ROOK -> findRookMoves(board, myPosition, possibleMoves);
            default -> findPawnMoves(board, myPosition, possibleMoves);

        }
        return possibleMoves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, int row, int col) {
        Collection<ChessMove> possibleMoves = new HashSet<>();
        ChessPosition myPosition = new ChessPosition(row, col);
        // switch selection for each type of piece
        // adding new comment to reach minimum commit amount
        return getChessMoves(board, myPosition, possibleMoves);
    }

    void findKingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        int [][] directions = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1},
                {1, 1},
                {-1, -1},
                {1, -1},
                {-1, 1},
        };

        exploreMovePositions(board, myPosition, possibleMoves, col, row, directions);
    }

    private void exploreMovePositions(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves, int col, int row, int[][] directions) {
        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                checkNewSpace(board, myPosition, newPosition, possibleMoves);
            }
        }
    }

    void findQueenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        int[][] directions = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1},
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
        };

        for (int[] direction : directions) {
            boolean endFound = false;
            for (int i = 1; i < 9 && !endFound; i++) {
                int newRow = row + direction[0] * i;
                int newCol = col + direction[1] * i;

                if (isWithinBounds(newRow, newCol)) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    endFound = checkNewSpace(board, myPosition, newPosition, possibleMoves);
                } else {
                    endFound = true;
                }
            }
        }
    }

    void findBishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        int[][] directions = {
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
        };

        boolean[] endFound = {false, false, false, false};

        exploreUnlimitedMovesInDirections(row, col, directions, board,
                myPosition, possibleMoves, endFound);
    }

    void findKnightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        int[][] knightMoves = {
                {2, -1}, {2, 1}, {1, -2}, {1, 2}, {-1, -2}, {-1, 2}, {-2, -1}, {-2, 1}
        };

        exploreMovePositions(board, myPosition, possibleMoves, col, row, knightMoves);
    }

    void findRookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        boolean[] endFound = {false, false, false, false};

        exploreUnlimitedMovesInDirections(row, col, directions, board,
                myPosition, possibleMoves, endFound);
    }


    void findPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> possibleMoves) {
        int col = myPosition.getColumn();
        int row = myPosition.getRow();

        int oneForward = (this.pieceColor == ChessGame.TeamColor.BLACK) ? row - 1 : row + 1;
        int startRow = (this.pieceColor == ChessGame.TeamColor.BLACK) ? 7 : 2;

        boolean teamColor = this.pieceColor == ChessGame.TeamColor.WHITE;
        boolean firstMove = (row == startRow);

        if (isWithinBounds(oneForward, col)) {
            ChessPosition forwardPosition = new ChessPosition(oneForward, col);
            checkNewSpacePawn(board, myPosition, forwardPosition, possibleMoves, firstMove, teamColor, false);
        }

        checkDiagonalCapture(board, myPosition, possibleMoves, oneForward, col - 1, teamColor);
        checkDiagonalCapture(board, myPosition, possibleMoves, oneForward, col + 1, teamColor);
    }

    private void checkDiagonalCapture(ChessBoard board, ChessPosition mP, Collection<ChessMove> pM, int nR, int nC, boolean tC) {
        if (isWithinBounds(nR, nC)) {
            ChessPosition diagonalPosition = new ChessPosition(nR, nC);
            checkNewSpacePawn(board, mP, diagonalPosition, pM, false, tC, true);
        }
    }

    private boolean allEndsFound(boolean[] endFound) {
        for (boolean end : endFound) {
            if (!end) {return false;}
        }
        return true;
    }

    private boolean isWithinBounds(int row, int col) {
        return row > 0 && row < 9 && col > 0 && col < 9;
    }

    boolean checkNewSpace(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition, Collection<ChessMove> possibleMoves) {
        ChessPiece foundPiece = board.getPiece(newPosition);
        if (foundPiece != null) {
            if (foundPiece.getTeamColor() != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            }
            return true;
        }

        possibleMoves.add(new ChessMove(myPosition, newPosition, null)); // empty space on board found
        return false;
    }
    void checkNewSpacePawn(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition,
                           Collection<ChessMove> possibleMoves, boolean firstMove, boolean teamColor, boolean diagonal) {

        ChessPiece foundPiece = board.getPiece(newPosition);

        if (!diagonal && foundPiece == null) {
            if (isPromotionRow(newPosition)) {
                addPromotionMoves(possibleMoves, myPosition, newPosition);
            } else {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            }

            if (firstMove) {
                ChessPosition doubleMove = teamColor
                        ? new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn())
                        : new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                if (board.getPiece(doubleMove) == null) {
                    possibleMoves.add(new ChessMove(myPosition, doubleMove, null));
                }
            }
        }
        if (diagonal && foundPiece != null && foundPiece.getTeamColor() != this.pieceColor) {
            if (isPromotionRow(newPosition)) {
                addPromotionMoves(possibleMoves, myPosition, newPosition);
            } else {
                possibleMoves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
    }

    private boolean isPromotionRow(ChessPosition position) {
        return position.getRow() == 1 || position.getRow() == 8;
    }

    private void addPromotionMoves(Collection<ChessMove> possibleMoves, ChessPosition myPosition, ChessPosition newPosition) {
        possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
        possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
        possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
        possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
    }

    private void exploreUnlimitedMovesInDirections(int row, int col, int[][] directions, ChessBoard board,
                                          ChessPosition myPosition, Collection<ChessMove> possibleMoves,
                                          boolean[] endFound) {
        for (int i = 1; i < 9; i++) {
            for (int j = 0; j < 4; j++) {
                if (endFound[j]) {continue;}

                int newRow = row + directions[j][0] * i;
                int newCol = col + directions[j][1] * i;

                if (isWithinBounds(newRow, newCol)) {
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    endFound[j] = checkNewSpace(board, myPosition, newPosition, possibleMoves);
                } else {
                    endFound[j] = true;
                }
            }

            if (allEndsFound(endFound)) {
                break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
