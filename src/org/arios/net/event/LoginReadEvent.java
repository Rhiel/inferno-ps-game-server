package org.arios.net.event;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.SQLException;

import org.arios.cache.Cache;
import org.arios.cache.crypto.ISAACCipher;
import org.arios.cache.crypto.ISAACPair;
import org.arios.cache.crypto.XTEACryption;
import org.arios.cache.misc.buffer.ByteBufferUtils;
import org.arios.game.node.entity.player.info.PlayerDetails;
import org.arios.game.node.entity.player.info.UIDInfo;
import org.arios.game.node.entity.player.info.login.LoginParser;
import org.arios.game.node.entity.player.info.login.LoginType;
import org.arios.game.node.entity.player.info.login.Response;
import org.arios.game.node.entity.player.info.portal.PlayerSQLManager;
import org.arios.game.system.task.TaskExecutor;
import org.arios.net.Constants;
import org.arios.net.IoReadEvent;
import org.arios.net.IoSession;
import org.arios.net.amsc.WorldCommunicator;
import org.arios.tools.StringUtils;

/**
 * Handles login reading events.
 *
 * @author Emperor
 */
public final class LoginReadEvent extends IoReadEvent {

    /**
     * The RSA exponent.
     */
    public static final BigInteger RSA_KEY = new BigInteger("65537");
    /**
     * The RSA modulus.
     */
    public static final BigInteger MODULUS = new BigInteger("94904992129904410061849432720048295856082621425118273522925386720620318960919649616773860564226013741030211135158797393273808089000770687087538386210551037271884505217469135237269866084874090369313013016228010726263597258760029391951907049483204438424117908438852851618778702170822555894057960542749301583313");

    /**
     * Constructs a new {@code LoginReadEvent}.
     *
     * @param session The session.
     * @param buffer  The buffer with data to read from.
     */
    public LoginReadEvent(IoSession session, ByteBuffer buffer) {
        super(session, buffer);
    }

    @Override
    public void read(IoSession session, ByteBuffer buffer) {
        int opcode = buffer.get() & 0xFF;
        if ((buffer.getShort()) != buffer.remaining()) {
            session.write(Response.BAD_SESSION_ID);
            return;
        }
        if (buffer.getInt() != Constants.REVISION) {
            session.write(Response.UPDATED);
            return;
        }
        switch (opcode) {
            case 16: // Reconnect world login
            case 18: // World login
                decodeWorld(opcode, session, buffer);
                break;
            default:
                System.err.println("[Login] Unhandled login type [opcode=" + opcode + "]!");
                session.disconnect();
                break;
        }
    }

    /**
     * Decodes a world login request.
     *
     * @param session The session.
     * @param rsa_buffer  The buffer to read from.
     */
    private static void decodeWorld(final int opcode, final IoSession session, ByteBuffer buffer) {

        ByteBuffer rsa_buffer = getRSABlock(buffer);

        int op = rsa_buffer.get();

        if(op != 1)
            return;

        int auth_type = rsa_buffer.get();

        int[] xtea = new int[]{rsa_buffer.getInt(), rsa_buffer.getInt(), rsa_buffer.getInt(), rsa_buffer.getInt()};

        switch (auth_type) {
            case 0:
                int trusted = rsa_buffer.getInt();
                rsa_buffer.position(rsa_buffer.position() + 4);
                break;
            case 1:
            case 3:
                int code = ByteBufferUtils.getTriByte(rsa_buffer);
                rsa_buffer.position(rsa_buffer.position() + 5);
                break;
            default:
                rsa_buffer.position(rsa_buffer.position() + 8);
                break;
        }

        String password = ByteBufferUtils.getString(rsa_buffer);

        System.out.println(password);

        byte[] block = new byte[rsa_buffer.remaining()];

        rsa_buffer.get(block);

        rsa_buffer = ByteBuffer.wrap(XTEACryption.decrypt(xtea, block, 0, block.length));

        String username = ByteBufferUtils.getString(rsa_buffer);

        int displaySetting = rsa_buffer.get();

        int windowMode = (displaySetting >> 1) + 1;

        System.out.println("Window Mode: "+windowMode);

        boolean lowMem = (displaySetting & 0xFF) == 1;

        int width = rsa_buffer.getShort();

        int height = rsa_buffer.getShort();

        rsa_buffer.position(rsa_buffer.position() + 24);

        String sessionToken1 = ByteBufferUtils.getString(rsa_buffer);

        int affiliateID = rsa_buffer.getInt();

        rsa_buffer.position(rsa_buffer.position() + 17);

        ByteBufferUtils.getJagString(rsa_buffer);

        ByteBufferUtils.getJagString(rsa_buffer);

        ByteBufferUtils.getJagString(rsa_buffer);

        ByteBufferUtils.getJagString(rsa_buffer);

        rsa_buffer.get();

        rsa_buffer.getShort();

        ByteBufferUtils.getJagString(rsa_buffer);

        ByteBufferUtils.getJagString(rsa_buffer);

        rsa_buffer.get();

        rsa_buffer.get();

        rsa_buffer.getInt();

        rsa_buffer.getInt();

        rsa_buffer.getInt();

        rsa_buffer.getInt();

        int sessionToken2 = rsa_buffer.get();

        int crcBlock = rsa_buffer.getInt();

        //No random.dat writing in 464 lmao
        //No affiliate id in our rev
        for (int i = 0; i < 17; i++) {
            int crc = Cache.getIndexes()[i] == null ? 0 : Cache.getIndexes()[i].getInformation().getInformationContainer().getCrc();
            if (crc != rsa_buffer.getInt() && crc != 0) {
                session.write(Response.UPDATED);
                return;
            }
        }
        ISAACCipher inCipher = new ISAACCipher(xtea);
        for (int i = 0; i < 4; i++) {
            xtea[i] += 50;
        }
        ISAACCipher outCipher = new ISAACCipher(xtea);
        session.setIsaacPair(new ISAACPair(inCipher, outCipher));

        final PlayerDetails details = new PlayerDetails(username, null, session);
        final ByteBuffer b = rsa_buffer;
        TaskExecutor.executeSQL(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = PlayerSQLManager.getCredentialResponse(details, username, password);
                    if (response != Response.SUCCESSFUL) {
                        session.write(response, true);
                        return;
                    }
                    login(details, username, password, session, b, opcode);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Handles the login procedure after we check an acc is registered.
     *
     * @param username the username.
     * @param password the password.
     * @param session  the session.
     * @param buffer   the byte buffer.
     * @param opcode   the opcode.
     */
    private static void login(final PlayerDetails details, String username, String password, IoSession session, ByteBuffer buffer, int opcode) {
        LoginParser parser = new LoginParser(details, password, new UIDInfo(session.getAddress(), ByteBufferUtils.getString(buffer), ByteBufferUtils.getString(buffer), ByteBufferUtils.getString(buffer)), LoginType.fromType(opcode));
        if (WorldCommunicator.isEnabled()) {
            WorldCommunicator.register(parser);
        } else {
            TaskExecutor.executeSQL(parser);
        }
    }


    /**
     * Gets the ISAAC seed from the buffer.
     *
     * @param buffer The buffer to read from.
     * @return The ISAAC seed.
     */
    public static int[] getISAACSeed(ByteBuffer buffer) {
        int[] seed = new int[4];
        for (int i = 0; i < 4; i++) {
            seed[i] = buffer.getInt();
        }
        return seed;
    }

    /**
     * Gets the RSA block buffer.
     *
     * @param buffer The buffer to get the RSA block from.
     * @return The RSA block buffer.
     */
    public static ByteBuffer getRSABlock(ByteBuffer buffer) {
        byte[] rsaData = new byte[buffer.getShort()];
        buffer.get(rsaData);
        ByteBuffer block = ByteBuffer.wrap(new BigInteger(rsaData).modPow(RSA_KEY, MODULUS).toByteArray());
        int num = block.get();
        if (num != 1) {
            throw new IllegalArgumentException("Invalid RSA Magic Number " + num + "!");
        }
        return block;
    }

}