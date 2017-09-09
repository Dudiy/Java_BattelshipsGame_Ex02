package gameLogic.game.board;

import java.awt.*;
import java.io.Serializable;

public class BoardCoordinates implements Serializable {
    private char col;
    private int row;

    public BoardCoordinates(char col, int row) {
        this.col = col;
        this.row = row;
    }

    public BoardCoordinates(BoardCoordinates position) {
        this.col = position.col;
        this.row = position.row;
    }

    // ======================================= getters =======================================
    public char getCol() {
        return col;
    }

    public int getColIndexInMemory() {
        return col - 'A';
    }

    public int getRowIndexInMemory() {
        return row - 1;
    }

    public static BoardCoordinates convertFromXmlToBoard(int row, int col) {
        // input from xml starts from 1,1 but board starts from 0,0
        char tempCol = (char) ('A' + col - 1);
        return new BoardCoordinates(tempCol, row);
    }

    // ======================================= methods =======================================
    // convert a string of type "A1" or "1A" to coordinates
    // input of 0 returns null value (for cancel)
    public static BoardCoordinates Parse(String stringToParse) throws IllegalArgumentException {
        BoardCoordinates res;

        if (stringToParse.length() != 2) {
            throw new IllegalArgumentException("Input string must be exactly 2 characters long");
        } else {
            char firstCharInString = stringToParse.charAt(0);
            char secondCharInString = stringToParse.charAt(1);
            char upperChar;
            int digit;

            if (Character.isAlphabetic(firstCharInString) && Character.isDigit(secondCharInString) && secondCharInString != '0') {
                upperChar = Character.toUpperCase(firstCharInString);
                digit = secondCharInString - '0';
            } else if (Character.isDigit(firstCharInString) && firstCharInString != '0' && Character.isAlphabetic(secondCharInString)) {
                upperChar = Character.toUpperCase(secondCharInString);
                digit = secondCharInString - '0';
            } else {
                throw new IllegalArgumentException("Input string must be a digit (greater than 0) and a char (in any order), for example: \"A1\" or \"1A\"");
            }

            res = new BoardCoordinates(upperChar, digit);
        }

        return res;
    }

    // converts two indices starting at 0,0 to boardCoordinates
    public static BoardCoordinates Parse(int row, int col) {
        return new BoardCoordinates((char) ('A' + col), row + 1);
    }

    public static BoardCoordinates Parse(Point point) {
        return BoardCoordinates.Parse(point.x, point.y);
    }

    // returns the index of the cell as it is in the memory (starting from 0,0)
    public static Point coordinatesToPoint(BoardCoordinates position) {
        return new Point(position.getRowIndexInMemory(), position.getColIndexInMemory());
    }

    // updates the value of row, does not check if the value is on the board!
    public void OffsetRow(int offset) {
        row += offset;
    }

    // updates the value of col, does not check if the value is on the board!
    public void offsetCol(int offset) {
        col += offset;
    }

    @Override
    public String toString() {
        return String.format("(%c,%d)", col, row);
    }
}
