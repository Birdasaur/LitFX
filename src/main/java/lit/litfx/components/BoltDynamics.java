package lit.litfx.components;

import java.util.function.Consumer;

/**
 *
 * @author phillsm1
 */
public class BoltDynamics {
    public double density;
    public double sway;
    public double jitter;
    public double envelopeSize;
    public double envelopeScalar;
    public boolean loopEndNode;
    public boolean loopStartNode;
            
    public BoltDynamics(Builder Builder) {
        this.density = Builder.density;
        this.sway = Builder.sway;
        this.jitter = Builder.jitter;
        this.envelopeSize = Builder.envelopeSize;
        this.envelopeScalar = Builder.envelopeScalar;
        this.loopEndNode = Builder.loopEndNode;
        this.loopStartNode = Builder.loopStartNode;
    }

    public static class Builder {

        public double density;
        public double sway;
        public double jitter;
        public double envelopeSize;
        public double envelopeScalar;
        public boolean loopEndNode;
        public boolean loopStartNode;
    
        public Builder with(Consumer<Builder> function) {
            function.accept(this);
            return this;
        }

        public BoltDynamics build() {
            return new BoltDynamics(this);
        }
    }
}
