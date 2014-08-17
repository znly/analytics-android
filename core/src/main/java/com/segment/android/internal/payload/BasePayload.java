/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Segment.io, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.segment.android.internal.payload;

import com.segment.android.AnalyticsContext;
import com.segment.android.Options;
import com.segment.android.internal.util.ISO8601Time;

/**
 * A payload object that will be sent to the server. Clients will not decode instances of this
 * directly, but through one if it's subclasses.
 */
/* This ignores projectId, receivedAt, messageId, sentAt, version that are set by the server. */
public class BasePayload {
  enum Type {
    alias, group, identify, page, screen, track
  }

  private enum Channel {
    browser, mobile, server
  }

  /**
   * The type of message.
   */
  private final Type type;

  /**
   * The anonymous ID is an identifier that uniquely (or close enough) identifies the user, but
   * isn't from your database. This is useful in cases where you are able to uniquely identifier
   * the
   * user between visits before they signup thanks to a cookie, or session ID or device ID. In our
   * mobile and browser libraries we will automatically handle sending the anonymous ID.
   */
  private final String anonymousId;

  /**
   * The channel where the request originated from: server, browser or mobile. In the future we may
   * add additional channels as we add libraries, for example console.
   * <p/>
   * This is always {@link Channel#MOBILE} for us.
   */
  private final Channel channel = Channel.mobile;

  /**
   * The context is a dictionary of extra information that provides useful context about a message,
   * for example ip address or locale. This dictionary is loosely speced, but you can also add your
   * own context, for example app.name or app.version. Check out the existing spec'ed properties in
   * the context before adding your own.
   */
  private final AnalyticsContext context;

  /**
   * The sent timestamp is an ISO-8601-formatted string that, if present on a message, can be used
   * to correct the original timestamp in situations where the local clock cannot be trusted, for
   * example in our mobile libraries. The sentAt and receivedAt timestamps will be assumed to have
   * occurred at the same time, and therefore the difference is the local clock skew.
   * <p/>
   * Mutable in case of upload failures.
   */
  private long sentAt;

  /** The timestamp when the message took place. This should be an ISO-8601-formatted string. */
  private final long timestamp;

  /**
   * The user ID is an identifier that unique identifies the user in your database. Ideally it
   * should not be an email address, because emails can change, whereas a database ID can't.
   */
  private final String userId;

  // todo: integrations

  public BasePayload(Type type, String anonymousId, AnalyticsContext context, String userId,
      Options options) {
    this.type = type;
    this.anonymousId = anonymousId;
    this.context = context;
    this.userId = userId;
    this.timestamp = options.getTimestamp() == 0L ? ISO8601Time.now().time()
        : ISO8601Time.from(options.getTimestamp()).time();
  }

  public Type getType() {
    return type;
  }

  public void setSentAt(long sentAt) {
    this.sentAt = sentAt;
  }
}