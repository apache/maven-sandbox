package org.apache.maven.project.interpolation.policies;

import org.apache.maven.project.interpolation.ModelPropertyPolicy;
import org.apache.maven.project.interpolation.ModelProperty;
import org.apache.maven.project.interpolation.ModelInterpolationException;
import org.apache.maven.model.Model;

import java.util.Date;
import java.text.DateFormat;

public class TimestampPolicy implements ModelPropertyPolicy {

    private String date;

    public TimestampPolicy() {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.LONG);
        date = dateFormatter.format(new Date());
    }

    public void evaluate(ModelProperty mp, Model model) throws ModelInterpolationException {
        if (model == null) {
            throw new IllegalArgumentException("model");
        }
        if (mp == null) {
            throw new IllegalArgumentException("mp");
        }

        if (mp.getValue() == null && mp.getExpression().equals("build.timestamp")) {
            mp.setValue(date);
        }
    }
}
