package src;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class LoginBrowser {
	private String tokenKey = "";
	private String expiresKey = "";
	private String useridKey = "";
	private Browser browser;
	private Display display;
	private String strToParse = "";
	private boolean downloadStarted = false;

	public static void main(String[] args) {
		LoginBrowser lb = new LoginBrowser();
		lb.run();
	}

	public void run() {
		final String newTitle = "VKauthorization";
		display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText(newTitle);
		browser = new Browser(shell, SWT.NONE);

		browser.addTitleListener(new TitleListener() {
			public void changed(TitleEvent event) {
				if (event.title
						.indexOf("https://api.vkontakte.ru/method/audio.get.xml") != -1) {
					if (System.getProperties().getProperty("os.name")
							.equals("WindowsXP")
							|| System.getProperties().getProperty("os.name")
									.equals("Windows XP")) {
						if (browser
								.getText()
								.indexOf(
										"<SPAN class=b>&nbsp;</SPAN> <SPAN class=m>&lt;</SPAN><SPAN class=t>url</SPAN><SPAN class=m>&gt;</SPAN><SPAN class=tx>") != -1) {
							if (strToParse.equals(""))
								strToParse = browser.getText();
							if (!downloadStarted) {
								downloadStarted = true;
								download();
							}
						}
					} else if (System.getProperties().getProperty("os.name")
							.equals("Windows 7")
							) {
						if (browser.getText().indexOf("<audio>") != -1) {
							if (strToParse.equals(""))
								strToParse = browser.getText();
							if (!downloadStarted) {
								downloadStarted = true;
								download7();
							}
						}
					}
				}
			}
		});

		browser.addLocationListener(new LocationListener() {
			public void changed(LocationEvent event) {
				// shell.setText(event.location);
				if (event.location.indexOf("access_token") != -1
						&& tokenKey.equals("") && expiresKey.equals("")
						&& useridKey.equals("")) {
					tokenKey = event.location.split("=")[1].split("&")[0];
					expiresKey = event.location.split("=")[2].split("&")[0];
					useridKey = event.location.split("=")[3];
					connect();
				}
			}

			public void changing(LocationEvent arg0) {
			}
		});

		browser.setUrl("http://api.vkontakte.ru/oauth/authorize?client_id=2223684&scope=audio&redirect_uri=http://api.vk.com/blank.html&display=page&response_type=token");
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private void connect() {
		browser.setUrl("https://api.vkontakte.ru/method/audio.get.xml?uid="
				+ useridKey + "&access_token=" + tokenKey);
	}

	private void download() {
		String[] audiostack = strToParse
				.split("<DIV style=\"TEXT-INDENT: -2em; MARGIN-LEFT: 1em\">"
						+ "<SPAN class=b>&nbsp;</SPAN> <SPAN class=m>&lt;</SPAN><SPAN class=t>url</SPAN><SPAN class=m>&gt;</SPAN><SPAN class=tx>");
		JOptionPane
				.showMessageDialog(
						null,
						"Найдено "
								+ (audiostack.length - 1)
								+ " музыкальных композиций.\n"
								+ "Далее начнётся процесс скачивания мызки на локальный компьютер.",
						"Уведомление", JOptionPane.INFORMATION_MESSAGE);
		Shell s = new Shell(display);
		s.setSize(400, 400);
		DirectoryDialog dlg = new DirectoryDialog(s);
		dlg.setText("Выбор каталога");
		dlg.setMessage("Выберите путь для сохранения музыки");
		String dir = dlg.open();
		// Progress prg = new Progress();
		String url = "";
		String fileName = "";
		for (int i = 1; i < audiostack.length; i++) {
			// Парсим список на предмет ссылок и наименований
			url = audiostack[i].split("</SPAN>")[0];
			try {
				fileName = audiostack[i]
						.split("artist</SPAN><SPAN class=m>&gt;</SPAN><SPAN class=tx>")[1]
						.split("</SPAN>")[0]
						+ " - "
						+ audiostack[i]
								.split("title</SPAN><SPAN class=m>&gt;</SPAN><SPAN class=tx>")[1]
								.split("</SPAN>")[0] + ".mp3";
			} catch (ArrayIndexOutOfBoundsException aie) {
				fileName = "unknown" + i;
			}
			// Скачиваем файл
			URLDownload.fileURL(url, fileName, dir);
		}
		JOptionPane.showMessageDialog(null, "Downloading completed",
				"Completed!", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}

	private void download7() {
		String[] audiostack = strToParse.split("<audio>");
		JOptionPane
				.showMessageDialog(
						null,
						"Найдено "
								+ (audiostack.length - 1)
								+ " музыкальных композиций.\n"
								+ "Далее начнётся процесс скачивания мызки на локальный компьютер.",
						"Уведомление", JOptionPane.INFORMATION_MESSAGE);
		Shell s = new Shell(display);
		s.setSize(400, 400);
		DirectoryDialog dlg = new DirectoryDialog(s);
		dlg.setText("Выбор каталога");
		dlg.setMessage("Выберите путь для сохранения музыки");
		String dir = dlg.open();
		// Progress prg = new Progress();
		String url = "";
		String fileName = "";
		for (int i = 1; i < audiostack.length; i++) {
			// Парсим список на предмет ссылок и наименований
			System.out.println(audiostack[i]);
			url = audiostack[i].split("url>")[1].substring(0,
					audiostack[i].split("url>")[1].length() - 2);
			try {
				fileName = audiostack[i].split("artist>")[1].substring(0,
						audiostack[i].split("artist>")[1].length() - 2)
						+ " - "
						+ audiostack[i].split("title>")[1].substring(0,
								audiostack[i].split("title>")[1].length() - 2)
						+ ".mp3";
			} catch (ArrayIndexOutOfBoundsException aie) {
				fileName = "unknown" + i + ".mp3";
			}
			// Скачиваем файл
			URLDownload.fileURL(url, fileName, dir);
		}
		JOptionPane.showMessageDialog(null, "Downloading completed",
				"Completed!", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}
}

class Progress {
	public static void main(String args[]) {
		JFrame f = new JFrame("Процесс загрузки...");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = f.getContentPane();
		JProgressBar progressBar = new JProgressBar();
		progressBar.setValue(25);
		progressBar.setStringPainted(true);
		Border border = BorderFactory.createTitledBorder("Загружается...");
		progressBar.setBorder(border);
		content.add(progressBar, BorderLayout.NORTH);
		f.setSize(300, 100);
		f.setVisible(true);
	}
}

class URLDownload {
	final static int size = 1024;

	@SuppressWarnings("unused")
	public static void fileURL(String fAddress, String localFileName,
			String destinationDir) {
		OutputStream outStream = null;
		URLConnection uCon = null;
		InputStream is = null;
		try {
			URL Url;
			byte[] buf;
			int ByteRead, ByteWritten = 0;
			Url = new URL(fAddress);
			outStream = new BufferedOutputStream(new FileOutputStream(
					destinationDir + "\\" + localFileName));
			uCon = Url.openConnection();
			is = uCon.getInputStream();
			buf = new byte[size];
			while ((ByteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, ByteRead);
				ByteWritten += ByteRead;
			}
			JOptionPane.showMessageDialog(null, "File " + localFileName
					+ " was downloaded successfully.", "Downloaded",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			JOptionPane
					.showMessageDialog(
							null,
							"File "
									+ localFileName
									+ " couldn't be downloaded because of the link parsing error. Link - "
									+ fAddress, "Error",
							JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		} finally {
			try {
				is.close();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NullPointerException npe) {
				// Сообщение выведено выше с exception о том, что ошибка
				// парсинга, поток не был открыт
			}
		}
	}

	public static void fileDownload(String fAddress, String destinationDir) {
		int slashIndex = fAddress.lastIndexOf('/');
		int periodIndex = fAddress.lastIndexOf('.');
		String fileName = fAddress.substring(slashIndex + 1);
		if (periodIndex >= 1 && slashIndex >= 0
				&& slashIndex < fAddress.length() - 1) {
			fileURL(fAddress, fileName, destinationDir);
		} else {
			System.err.println("path or file name.");
		}
	}
}
