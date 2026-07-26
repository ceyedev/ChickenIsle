public class TimeManager
{
    public static TimeManager instance;
    public boolean isPaused = false;

    public TimeManager()
    {
        instance = this;
    }

    public void setPaused(boolean paused)
    {
        isPaused = paused;
    }
}