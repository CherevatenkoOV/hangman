package project1_Hangman;

import java.util.Scanner;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.io.File;

public class Hangman {
	public static ArrayList<String> wordList = new ArrayList<>();
	public static Random random = new Random();
	public static boolean gameIsOn = false;
	public static String hiddenWord = "";
	public static StringBuilder hiddenWordMask = new StringBuilder("");
	public static String GREETING_MESSAGE = """
			Вы начали новую игру!
			Угадайте слово:
			""";
	public static char enteredLetter = ' ';
	public static String outputProgress = "";
	public static int attempts;
	public static final int INITIAL_ATTEMPTS = 5;
	public static ArrayList<StringBuilder> hangmanStatus;
	public static final Scanner scanner = new Scanner(System.in);

	public static void getWordList() {
		System.out.println("Current working directory: " + System.getProperty("user.dir"));
		Path filePath = Paths.get("resources", "words.txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {

			String line;
			while ((line = reader.readLine()) != null) {
				wordList.add(line.toUpperCase());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void wantToPlay() {
		System.out.println("Приветствуем Вас в игре Виселица");
		playAgain();
	}

	public static void startNewGame() {
		gameIsOn = true;
		attempts = INITIAL_ATTEMPTS;
		getRandomWord();
		defineHiddenWordMask();
		setInitialHangmanStatus();
		printFirstMessage();
		getInput();
		handleInput();
	}

	public static boolean checkWin() {
		if (hiddenWordMask.indexOf("*") == -1) {
			return true;
		}
		return false;
	}

	public static void finishGame() {
		gameIsOn = false;
		System.out.println("Приходите еще, если захотите сыграть снова.");
		scanner.close();
	}

	public static void playAgain() {
		resetHiddenWordMask();

		while (true) {
			System.out.print("Хотите начать новую игру? (Y - да, N - нет): ");
			String answer = scanner.nextLine().toUpperCase();
			if (answer.equals("Y")) {
				startNewGame();
			} else if (answer.equals("N")) {
				finishGame();
				break;
			} else {
				System.out.println(
						"Вы ввели неправильный символ. Введите Y если хотите продолжить, или N если хотите закончить игру");
			}
		}

	}

	public static void getRandomWord() {
		int index = random.nextInt(wordList.size());
		hiddenWord = wordList.get(index);
	}

	public static void defineHiddenWordMask() {	
		for (int i = 0; i < hiddenWord.length(); i++) {
			hiddenWordMask.append("*");
		}
	}

	public static void setInitialHangmanStatus() {
		StringBuilder line1 = new StringBuilder("       ");
		StringBuilder line2 = new StringBuilder("       ");
		StringBuilder line3 = new StringBuilder("     ('_')          ");
		StringBuilder line4 = new StringBuilder("     / # \\          ");
		StringBuilder line5 = new StringBuilder("______|_|_____      ");
		StringBuilder line6 = new StringBuilder("||          ||      ");
		StringBuilder line7 = new StringBuilder("||          ||      ");
		StringBuilder line8 = new StringBuilder("||          ||      ");
		StringBuilder line9 = new StringBuilder("||          ||      ");
		StringBuilder line10 = new StringBuilder("||          ||      ");
		StringBuilder line11 = new StringBuilder("^^^^^^^^^^^^^^^^^^^^^^");
		hangmanStatus = new ArrayList<>(
				Arrays.asList(line1, line2, line3, line4, line5, line6, line7, line8, line9, line10, line11));
	}

	public static void updateHangmanStatus(int attempts) {
		switch (attempts) {
		case 4:
			hangmanStatus.get(9).append("||");
			hangmanStatus.get(8).append("||");
			break;
		case 3:
			hangmanStatus.get(7).append("||");
			hangmanStatus.get(6).append("||");
			break;
		case 2:
			hangmanStatus.get(5).append("||");
			hangmanStatus.get(4).append("||");
			break;
		case 1:
			hangmanStatus.get(3).append("||");
			hangmanStatus.get(2).append("||");
			break;
		case 0:
			hangmanStatus.get(2).replace(6, 9, "x_x");
			hangmanStatus.get(1).append("|            ||");
			hangmanStatus.get(0).append("===============");
			break;
		default:
			System.out.println("Something went wrong with switch-case statement");
		}
	}

	public static void printHangmanStatus() {
		for (StringBuilder line : hangmanStatus) {
			System.out.println(line);
		}
	}

	public static void printFirstMessage() {
		System.out.println(GREETING_MESSAGE + hiddenWordMask);
		printHangmanStatus();
		printAttempts();
	}

	public static char getInput() {
		System.out.println("");
		System.out.print("Введите букву: ");
		String input = scanner.nextLine();

		while (input.matches(".*\\d.*") || !checkInput(input)) {
			System.out.println("Необходимо ввести букву.");
			System.out.print("Введите букву: ");
			input = scanner.nextLine();
		}

		enteredLetter = input.toUpperCase().charAt(0);

		return enteredLetter;
	}

	public static boolean checkInput(String input) {
		if (input.trim().length() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static void handleInput() {

		boolean isSuccessfulAttempt = false;
		for (int i = 0; i < hiddenWord.length(); i++) {
			if (hiddenWord.charAt(i) == enteredLetter) {
				hiddenWordMask.setCharAt(i, enteredLetter);
				isSuccessfulAttempt = true;
			}
		}

		if (isSuccessfulAttempt == true) {
			handleCorrectInput();
		} else {
			handleWrongInput();
		}

		if (!checkWin()) {
			if (checkAttempts()) {
				getInput();
				handleInput();
			} else {
				System.out.println("Вы проиграли!");
			}
		} else {
			System.out.println("Поздравляем с победой!");
			System.out.println("Вы отгадали слово: " + hiddenWord);
		}
	}

	public static void handleCorrectInput() {
		printHangmanStatus();
		System.out.println(enteredLetter + " содержится в загаданном слове. Вы - молодец!");
		printAttempts();
		System.out.println(hiddenWordMask);
	}

	public static void handleWrongInput() {
		attempts -= 1;
		updateHangmanStatus(attempts);
		printHangmanStatus();
		System.out.println("Вы ввели букву " + enteredLetter + " и не угадали.");
		if (checkAttempts()) {
			System.out.println("Попробуйте еще.");
		}
		printAttempts();
		System.out.println(hiddenWordMask);
	}

	public static void decipherHiddenWordMask(String letter, int index) {
		hiddenWordMask.replace(index, index + 1, letter);
	}

	public static void printAttempts() {
		System.out.println("Количество попыток: " + attempts);
	}

	public static boolean checkAttempts() {
		if (attempts > 0) {
			return true;
		}
		return false;
	}

	public static void resetEnteredLetter() {
		enteredLetter = ' ';
	}

	public static void resetHiddenWordMask() {
		hiddenWordMask = new StringBuilder("");
	}

	public static void main(String[] args) {
		getWordList();
		wantToPlay();
		while (gameIsOn) {
			playAgain();
		}
	}

}
