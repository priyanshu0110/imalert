package com.yesfoss.imworker.dal;

public class DALManager {
  private static final DALManager INSTANCE = new DALManager();
  private DataAccessLayer dal;

  private DALManager() {}

  private static DALManager getInstance() {
    return INSTANCE;
  }

  public static DataAccessLayer getDal() {
    return getInstance().dal;
  }

  private void setDal(DataAccessLayer dal) {
    this.dal = dal;
  }

  public static void initialize() {
    synchronized (DALManager.class) {
      if (getDal() == null) {
        getInstance().setDal(new DataAccessLayer());
      }
    }
  }

}
