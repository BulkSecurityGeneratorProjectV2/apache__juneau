// ***************************************************************************************************************************
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file *
// * distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file        *
// * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance            *
// * with the License.  You may obtain a copy of the License at                                                              *
// *                                                                                                                         *
// *  http://www.apache.org/licenses/LICENSE-2.0                                                                             *
// *                                                                                                                         *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  *
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the        *
// * specific language governing permissions and limitations under the License.                                              *
// ***************************************************************************************************************************
package org.apache.juneau.utils;

import static org.apache.juneau.internal.IOUtils.*;
import static org.apache.juneau.internal.ThrowableUtils.*;

import java.io.*;
import java.util.*;

import org.apache.juneau.internal.*;

/**
 * A utility class for piping input streams and readers to output streams and writers.
 *
 * <p>
 * A typical usage is as follows...
 * <p class='bcode w800'>
 * 	InputStream in = getInputStream();
 * 	Writer out = getWriter();
 * 	IOPipe.create(in, out).closeOut().run();
 * </p>
 *
 * <p>
 * By default, the input stream is closed and the output stream is not.
 * This can be changed by calling {@link #closeOut()} and {@link #close(boolean, boolean)}.
 */
public class IOPipe {

	private Object input, output;
	private boolean byLines;
	private boolean closeIn = true, closeOut;
	private int buffSize = 1024;
	private LineProcessor lineProcessor;

	private IOPipe(Object input, Object output) {
		assertFieldNotNull(input, "input");
		assertFieldNotNull(output, "output");

		if (input instanceof InputStream || input instanceof Reader || input instanceof File || input instanceof byte[] || input instanceof CharSequence || input == null)
			this.input = input;
		else
			illegalArg("Invalid input class type.  Must be one of the following:  InputStream, Reader, CharSequence, byte[], File");

		if (output instanceof OutputStream || output instanceof Writer)
			this.output = output;
		else
			illegalArg("Invalid output class type.  Must be one of the following:  OutputStream, Writer");
	}

	/**
	 * Creates a new pipe with the specified input and output.
	 *
	 * @param input The input.  Must be one of the following types:  Reader, InputStream, CharSequence.
	 * @param output The output.  Must be one of the following types:  Writer, OutputStream.
	 * @return This object (for method chaining).
	 */
	public static IOPipe create(Object input, Object output) {
		return new IOPipe(input, output);
	}

	/**
	 * Close output after piping.
	 *
	 * @return This object (for method chaining).
	 */
	public IOPipe closeOut() {
		this.closeOut = true;
		return this;
	}

	/**
	 * Specifies whether to close the input and output after piping.
	 *
	 * @param in Close input stream.  Default is <jk>true</jk>.
	 * @param out Close output stream.  Default is <jk>false</jk>.
	 * @return This object (for method chaining).
	 */
	public IOPipe close(boolean in, boolean out) {
		this.closeIn = in;
		this.closeOut = out;
		return this;
	}

	/**
	 * Specifies the temporary buffer size.
	 *
	 * @param buffSize The buffer size.  Default is <c>1024</c>.
	 * @return This object (for method chaining).
	 */
	public IOPipe buffSize(int buffSize) {
		assertFieldPositive(buffSize, "buffSize");
		this.buffSize = buffSize;
		return this;
	}

	/**
	 * Specifies whether the content should be piped line-by-line.
	 *
	 * <p>
	 * This can be useful if you're trying to pipe console-based input.
	 *
	 * @param byLines Pipe content line-by-line.  Default is <jk>false</jk>.
	 * @return This object (for method chaining).
	 */
	public IOPipe byLines(boolean byLines) {
		this.byLines = byLines;
		return this;
	}

	/**
	 * Same as calling {@link #byLines()} with <jk>true</jk>.
	 *
	 * @return This object (for method chaining).
	 */
	public IOPipe byLines() {
		this.byLines = true;
		return this;
	}

	/**
	 * Specifies a line processor that can be used to process lines before they're piped to the output.
	 *
	 * @param lineProcessor The line processor.
	 * @return This object (for method chaining).
	 */
	public IOPipe lineProcessor(LineProcessor lineProcessor) {
		this.lineProcessor = lineProcessor;
		return this;
	}

	/**
	 * Interface to implement for the {@link #lineProcessor(LineProcessor)} method.
	 */
	public interface LineProcessor {
		/**
		 * Process the specified line.
		 *
		 * @param line The line to process.
		 * @return The processed line.
		 */
		public String process(String line);
	}

	/**
	 * Performs the piping of the input to the output.
	 *
	 * @return The number of bytes (if streams) or characters (if readers/writers) piped.
	 * @throws IOException
	 */
	public int run() throws IOException {

		int c = 0;

		try {
			if (input == null)
				return 0;

			if ((input instanceof InputStream || input instanceof byte[]) && output instanceof OutputStream && lineProcessor == null) {
				OutputStream out = (OutputStream)output;
				if (input instanceof InputStream) {
					InputStream in = (InputStream)input;
					byte[] b = new byte[buffSize];
					int i;
					while ((i = in.read(b)) > 0) {
						c += i;
						out.write(b, 0, i);
					}
				} else {
					byte[] b = (byte[])input;
					out.write(b);
					c = b.length;
				}
				out.flush();
			} else {
				@SuppressWarnings("resource")
				Writer out = (output instanceof Writer ? (Writer)output : new OutputStreamWriter((OutputStream)output, UTF8));
				closeIn |= input instanceof File;
				if (byLines || lineProcessor != null) {
					Reader in = null;
					if (input instanceof Reader)
						in = (Reader)input;
					else if (input instanceof InputStream)
						in = new InputStreamReader((InputStream)input, UTF8);
					else if (input instanceof File)
						in = new FileReader((File)input);
					else if (input instanceof byte[])
						in = new StringReader(new String((byte[])input, "UTF8"));
					else if (input instanceof CharSequence)
						in = new StringReader(input.toString());
					try (Scanner s = new Scanner(in)) {
						while (s.hasNextLine()) {
							String l = s.nextLine();
							if (lineProcessor != null)
								l = lineProcessor.process(l);
							if (l != null) {
								out.write(l);
								out.write("\n");
								out.flush();
								c += l.length() + 1;
							}
						}
					}
				} else {
					if (input instanceof InputStream)
						input = new InputStreamReader((InputStream)input, UTF8);
					else if (input instanceof File)
						input = new FileReader((File)input);
					else if (input instanceof byte[])
						input = new String((byte[])input, UTF8);

					if (input instanceof Reader) {
						Reader in = (Reader)input;
						int i;
						char[] b = new char[buffSize];
						while ((i = in.read(b)) > 0) {
							c += i;
							out.write(b, 0, i);
						}
					} else {
						String s = input.toString();
						out.write(s);
						c = s.length();
					}
				}
				out.flush();
			}
		} finally {
			closeQuietly(input, output);
		}
		return c;
	}

	private void closeQuietly(Object input, Object output) {
		if (closeIn)
			IOUtils.closeQuietly(input);
		if (closeOut)
			IOUtils.closeQuietly(output);
	}
}
