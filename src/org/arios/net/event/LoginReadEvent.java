package org.arios.net.event;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.SQLException;

import org.arios.cache.Cache;
import org.arios.cache.crypto.ISAACCipher;
import org.arios.cache.crypto.ISAACPair;
import org.arios.cache.crypto.XTEACryption;
import org.arios.cache.misc.buffer.ByteBufferUtils;
import org.arios.game.node.entity.player.info.ClientInfo;
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
     * The RSA modulus.
     */
    public static final BigInteger MODULUS = new BigInteger("123610787314551677907874298567344116546092825605786721563138484858373112431812744323286817950310420955185277119707409447778394330506113291931456288097283884073528921913702312357839491271114936987467508531371687701010613362457400925735299289293875139332422005763586035731230593313994568625608925020525012815769");
    /**
     * The RSA exponent.
     */
    public static final BigInteger RSA_KEY = new BigInteger("4064593232263589512664130390659117310173337796671657002434707644075774864436218685882525789751117035543651253383118961196918378660003877872229249139412037813980649248292120898174901611934400871795834821855106298536869547440918732619284043998938822097337735490034265919518682678773903983829362692278880641213");

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
     * @param session    The session.
     * @param rsa_buffer The buffer to read from.
     */
    private static void decodeWorld(final int opcode, final IoSession session, ByteBuffer buffer) {

        ByteBuffer rsa_buffer = getRSABlock(buffer);

        int auth_type = rsa_buffer.get();

        int[] xtea = new int[]{rsa_buffer.getInt(), rsa_buffer.getInt(), rsa_buffer.getInt(), rsa_buffer.getInt()};

        switch (auth_type) {
            case 1:
                rsa_buffer.position(rsa_buffer.position() + 8);
                break;
            case 2:
                rsa_buffer.getInt();
                rsa_buffer.position(rsa_buffer.position() + 4);
                break;
            case 0:
            case 3:
                int code = ByteBufferUtils.getTriByte(rsa_buffer);
                rsa_buffer.position(rsa_buffer.position() + 5);
                break;
        }

        String password = ByteBufferUtils.getString(rsa_buffer);

        ByteBuffer xtea_buffer = XTEACryption.decrypt(buffer, xtea);

        String username = ByteBufferUtils.getString(xtea_buffer);

        int displaySetting = xtea_buffer.get();

        int windowMode = (displaySetting >> 1) + 1;

        boolean lowMem = (displaySetting & 0xFF) == 1;

        int width = xtea_buffer.getShort();

        int height = xtea_buffer.getShort();

        xtea_buffer.position(xtea_buffer.position() + 24);

        String sessionToken1 = ByteBufferUtils.getString(xtea_buffer);

        int affiliateID = xtea_buffer.getInt();

        xtea_buffer.get(); // machine info opcode 6
        xtea_buffer.get(); // os type
        xtea_buffer.get(); // 64 bit
        xtea_buffer.get(); // os version
        xtea_buffer.get(); // vendor
        xtea_buffer.get(); // major
        xtea_buffer.get(); // minor
        xtea_buffer.get(); // patch
        xtea_buffer.get(); // some flag
        xtea_buffer.getShort(); // max memory
        xtea_buffer.get();
        xtea_buffer.get();
        xtea_buffer.getShort();
        xtea_buffer.getShort();
        ByteBufferUtils.getJagString(xtea_buffer);
        ByteBufferUtils.getJagString(xtea_buffer);
        ByteBufferUtils.getJagString(xtea_buffer);
        ByteBufferUtils.getJagString(xtea_buffer);
        xtea_buffer.get();
        xtea_buffer.getShort();
        ByteBufferUtils.getJagString(xtea_buffer);
        ByteBufferUtils.getJagString(xtea_buffer);
        xtea_buffer.get();
        xtea_buffer.get();

        xtea_buffer.getInt();
        xtea_buffer.getInt();
        xtea_buffer.getInt();
        xtea_buffer.getInt();

        int sessionToken2 = xtea_buffer.get();

        int crcBlock = xtea_buffer.getInt();

        //No random.dat writing in 464 lmao
        //No affiliate id in our rev
        for (int i = 0; i < 17; i++) {
            int crc = Cache.getIndexes()[i] == null ? 0 : Cache.getIndexes()[i].getInformation().getInformationContainer().getCrc();
            if (crc != xtea_buffer.getInt() && crc != 0) {
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
        details.setClientInfo(new ClientInfo(windowMode, width, height));
        final ByteBuffer b = xtea_buffer;
        TaskExecutor.executeSQL(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = PlayerSQLManager.getCredentialResponse(details, username, password);
                    if (response != Response.SUCCESSFUL) {
                        session.write(response, true);
                        return;
                    }
                    System.out.println("logging in");
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
        LoginParser parser = new LoginParser(details, password, new UIDInfo(session.getAddress(), "none", "none", "none"), LoginType.fromType(opcode));
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