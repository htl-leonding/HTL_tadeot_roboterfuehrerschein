package at.htl.leonding.pimpedhotroad.model;

import java.io.Serializable;

/**
 * Available signal types for the phone to send; Communication protocol.
 *
 * @author Bernard Marijanovic
 */
public enum Impulse
{
    // MOVEMENT
    FORWARD,
    RIGHT,
    BACKWARD,
    LEFT,
    STOP,

    // GENERAL
    QUIT,

    // MUSIC
    PLAY_SONG,
    PAUSE_SONG,
    NEXT_SONG,
    PREV_SONG
}