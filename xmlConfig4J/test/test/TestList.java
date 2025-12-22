package test;

import org.junit.jupiter.api.Test;

import static utils.Validator.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestList {
    @Test
    public void test() {
        validate(new ArrayList<Object>(List.of("Hello", "World!")));
        validate(new LinkedList<>(List.of("Hello", "World!")));
    }
}