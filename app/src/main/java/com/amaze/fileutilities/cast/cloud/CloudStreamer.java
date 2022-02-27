/*
 * Copyright (C) 2021-2020 Team Amaze - Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com>. All Rights reserved.
 *
 * This file is part of Amaze File Utilities.
 *
 * 'Amaze File Utilities' is a registered trademark of Team Amaze. All other product
 * and company names mentioned are trademarks or registered trademarks of their respective owners.
 */

package com.amaze.fileutilities.cast.cloud;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import android.util.Log;

public class CloudStreamer extends CloudStreamServer {

  private static final String TAG = CloudStreamer.class.getSimpleName();

  public static final int PORT = 7871;
  public static final String URL = "http://127.0.0.1:" + PORT;
  private InputStream inputStream;
  private String fileName;
  long length = 0;
  private static CloudStreamer instance;
  private static Pattern pattern =
      Pattern.compile(
          "^.*\\.(?i)(mp3|wma|wav|aac|ogg|m4a|flac|mp4|avi|mpg|mpeg|3gp|3gpp|mkv|flv|rmvb)$");

  // private CBItem source;
  // private String mime;

  protected CloudStreamer(int port) throws IOException {
    super(port, new File("."));
  }

  public static CloudStreamer getInstance() {
    if (instance == null)
      try {
        instance = new CloudStreamer(PORT);
      } catch (IOException e) {
        Log.e(TAG, "Error initializing CloudStreamer", e);
      }
    return instance;
  }

  /*public static boolean isStreamMedia(SmbFile file) {
    return pattern.matcher(file.getName()).matches();
  }*/

  public void setStreamSrc(InputStream inputStream, String fileName, long length) {
    this.inputStream = inputStream;
    this.fileName = fileName;
    this.length = length;
  }

  @Override
  public void stop() {
    super.stop();
    instance = null;
  }

  @Override
  public Response serve(
      String uri, String method, Properties header, Properties parms, Properties files) {
    Response res = null;

    if (inputStream == null) res = new Response(HTTP_NOTFOUND, MIME_PLAINTEXT, null);
    else {

      long startFrom = 0;
      long endAt = -1;
      String range = header.getProperty("range");
      if (range != null) {
        if (range.startsWith("bytes=")) {
          range = range.substring("bytes=".length());
          int minus = range.indexOf('-');
          try {
            if (minus > 0) {
              startFrom = Long.parseLong(range.substring(0, minus));
              endAt = Long.parseLong(range.substring(minus + 1));
            }
          } catch (NumberFormatException nfe) {
          }
        }
      }
      Log.d(TAG, "Request: " + range + " from: " + startFrom + ", to: " + endAt);

      // Change return code and add Content-Range header when skipping
      // is requested
      // source.open();
      final CloudStreamSource source = new CloudStreamSource(fileName, length, inputStream);
      long fileLen = source.length();
      if (range != null && startFrom > 0) {
        if (startFrom >= fileLen) {
          res = new Response(HTTP_RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, null);
          res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
        } else {
          if (endAt < 0) endAt = fileLen - 1;
          long newLen = fileLen - startFrom;
          if (newLen < 0) newLen = 0;
          Log.d(TAG, "start=" + startFrom + ", endAt=" + endAt + ", newLen=" + newLen);
          final long dataLen = newLen;
          source.moveTo(startFrom);
          Log.d(TAG, "Skipped " + startFrom + " bytes");

          res = new Response(HTTP_PARTIALCONTENT, null, source);
          res.addHeader("Content-length", "" + dataLen);
        }
      } else {
        source.reset();
        res = new Response(HTTP_OK, null, source);
        res.addHeader("Content-Length", "" + fileLen);
        //        res.addHeader("Content-Range", 0 + "-" + fileLen + "/" + fileLen);
        //        res.addHeader("Range", "bytes=0-" + fileLen);
      }
    }

    res.addHeader("Accept-Ranges", "bytes"); // Announce that the file
    //    res.addHeader("Content-Type", MimeTypes.APPLICATION_M3U8);
    //    res.addHeader("cross-origin-resource-policy", "same-origin");
    //    res.addHeader("Accept-Encoding", "gzip");
    // server accepts partial
    // content requestes
    return res;
  }
}
