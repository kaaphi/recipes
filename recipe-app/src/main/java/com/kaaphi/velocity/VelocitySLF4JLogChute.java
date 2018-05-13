package com.kaaphi.velocity;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocitySLF4JLogChute implements LogChute {
  private static final Logger log = LoggerFactory.getLogger("org.apache.velocity");

  @Override
  public void init(RuntimeServices rs) throws Exception {
    //noop
  }

  @Override
  public void log(int level, String message) {
    switch(level) {
      case LogChute.TRACE_ID:
        log.trace(message);
        break;
        
      case LogChute.DEBUG_ID:
        log.debug(message);
        break;
        
      case LogChute.INFO_ID:
        log.info(message);
        break;
        
      case LogChute.WARN_ID:
        log.warn(message);
        break;
        
      case LogChute.ERROR_ID:
        log.error(message);
        break;
        
      default:
        log.error("Unknown level: %d", level);
        log.error(message);
    }
  }

  @Override
  public void log(int level, String message, Throwable t) {
    switch(level) {
      case LogChute.TRACE_ID:
        log.trace(message, t);
        break;
        
      case LogChute.DEBUG_ID:
        log.debug(message, t);
        break;
        
      case LogChute.INFO_ID:
        log.info(message, t);
        break;
        
      case LogChute.WARN_ID:
        log.warn(message, t);
        break;
        
      case LogChute.ERROR_ID:
        log.error(message, t);
        break;
        
      default:
        log.error("Unknown level: %d", level);
        log.error(message, t);
    }
  }

  @Override
  public boolean isLevelEnabled(int level) {
    switch(level) {
      case DEBUG_ID: return log.isDebugEnabled();
      case ERROR_ID: return log.isErrorEnabled();
      case INFO_ID: return log.isInfoEnabled();
      case TRACE_ID: return log.isTraceEnabled();
      case WARN_ID: return log.isWarnEnabled();
      default: return false;
    }
  }

}
